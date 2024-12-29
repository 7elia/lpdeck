package me.elia.lpdeck.gui;

import me.elia.lpdeck.Lpdeck;
import me.elia.lpdeck.action.base.ActionRegistry;
import me.elia.lpdeck.launchpad.LaunchpadLightListener;
import net.thecodersbreakfast.lp4j.api.Pad;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PadPanel extends JPanel implements LaunchpadLightListener {
    private final int x;
    private final int y;
    private final boolean edge;

    public PadPanel(int x, int y) {
        this.x = x;
        this.y = y;
        this.edge = x == 8 || y == 0;

        if (this.x == 8 && this.y == 0) {
            this.setVisible(false);
        }

        this.setForeground(Color.GRAY);
        this.setFocusable(true);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.addMouseListener(new MouseListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }
                ActionRegistry registry = Lpdeck.getInstance().getActionRegistry();
                if (PadPanel.this.edge) {
                    registry.onButtonPressed(
                            PadPanel.this.x == 8
                                    ? net.thecodersbreakfast.lp4j.api.Button.atRight(PadPanel.this.y - 1)
                                    : net.thecodersbreakfast.lp4j.api.Button.atTop(PadPanel.this.x),
                            System.currentTimeMillis()
                    );
                } else {
                    registry.onPadPressed(Pad.at(PadPanel.this.x, PadPanel.this.y - 1), System.currentTimeMillis());
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) { }

            @Override
            public void mouseReleased(MouseEvent e) { }

            @Override
            public void mouseEntered(MouseEvent e) { }

            @Override
            public void mouseExited(MouseEvent e) { }
        });

        Lpdeck.getInstance().getLaunchpadClient().addLightListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int size = Math.min(this.getWidth(), this.getHeight());
        RadialGradientPaint paint = new RadialGradientPaint(
                new Point(this.getWidth() / 2, this.getHeight() / 2),
                size / 2.0F,
                new float[] { 0.0F, 1.0F },
                new Color[] { this.getForeground(), this.getForeground().darker() }
        );

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setPaint(paint);

        if (this.edge) {
            size = (int) (size * 0.85F);
            g2d.fillOval((this.getWidth() - size) / 2, (this.getHeight() - size) / 2, size, size);
        } else {
            g2d.fillRect((this.getWidth() - size) / 2, (this.getHeight() - size) / 2, size, size);
        }
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
        if (point.x == this.x && point.y == this.y) {
            Color swingColor = new Color(color.getRed() * 255 / 3, color.getGreen() * 255 / 3, 0);
            this.setForeground(swingColor);
        }
    }
}
