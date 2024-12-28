package me.elia.lpdeck.gui;

import me.elia.lpdeck.Lpdeck;
import me.elia.lpdeck.action.base.ActionRegistry;
import me.elia.lpdeck.launchpad.LaunchpadLightListener;
import net.thecodersbreakfast.lp4j.api.BackBufferOperation;
import net.thecodersbreakfast.lp4j.api.Pad;
import net.thecodersbreakfast.lp4j.api.ScrollSpeed;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Position;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

public class LpdeckFrame extends JFrame implements LaunchpadLightListener {
    private final Map<Point, JPanel> pads;

    public LpdeckFrame() {
        super("Lpdeck");

        this.pads = new HashMap<>();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 600);
        this.setResizable(true);

        this.createGrid();

        Lpdeck.getInstance().getLaunchpadClient().addLightListener(this);
    }

    private void createGrid() {
        JPanel gridPanel = new JPanel(new GridLayout(9, 9, 10, 10));
        gridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                int finalX = x;
                int finalY = y;
                boolean isEdge = x == 8 || y == 0;

                JPanel pad = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2d = (Graphics2D) g;

                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setColor(this.getForeground());

                        int size = Math.min(this.getWidth(), this.getHeight());
                        if (isEdge) {
                            size = (int) (size * .85F);
                            g2d.fillOval((this.getWidth() - size) / 2, (this.getHeight() - size) / 2, size, size);
                        } else {
                            g2d.fillRect((this.getWidth() - size) / 2, (this.getHeight() - size) / 2, size, size);
                        }
                    }
                };

                pad.setForeground(Color.GRAY);
                pad.setFocusable(true);
                pad.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                pad.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) { }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (e.getButton() != MouseEvent.BUTTON1) {
                            return;
                        }
                        ActionRegistry registry = Lpdeck.getInstance().getActionRegistry();
                        if (isEdge) {
                            registry.onButtonPressed(
                                    finalX == 8 ? net.thecodersbreakfast.lp4j.api.Button.atRight(finalY + 1) : net.thecodersbreakfast.lp4j.api.Button.atTop(finalX),
                                    System.currentTimeMillis()
                            );
                        } else {
                            registry.onPadPressed(Pad.at(finalX, finalY - 1), System.currentTimeMillis());
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) { }

                    @Override
                    public void mouseEntered(MouseEvent e) { }

                    @Override
                    public void mouseExited(MouseEvent e) { }
                });

                if (x == 8 && y == 0) {
                    pad.setVisible(false);
                }

                gridPanel.add(pad);

                this.pads.put(new Point(x, y), pad);
            }
        }

        this.add(gridPanel);
    }

    @Override
    public void onPadLightChange(Pad pad, net.thecodersbreakfast.lp4j.api.Color color) {
        this.changeColorAt(new Point(pad.getX(), pad.getY() + 1), color);
    }

    @Override
    public void onButtonLightChange(net.thecodersbreakfast.lp4j.api.Button button, net.thecodersbreakfast.lp4j.api.Color color) {
        this.changeColorAt(new Point(button.isTopButton() ? button.getCoordinate() : 8, button.isRightButton() ? button.getCoordinate() + 1 : 0), color);
    }

    private void changeColorAt(Point point, net.thecodersbreakfast.lp4j.api.Color color) {
        if (!this.pads.containsKey(point)) {
            return;
        }
        Color swingColor = new Color(color.getRed() * 255 / 3, color.getGreen() * 255 / 3, 0);
        this.pads.get(point).setForeground(swingColor);
    }
}
