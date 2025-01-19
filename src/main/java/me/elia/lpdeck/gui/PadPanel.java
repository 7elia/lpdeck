package me.elia.lpdeck.gui;

import lombok.Getter;
import me.elia.lpdeck.Lpdeck;
import me.elia.lpdeck.action.base.ActionRegistry;
import me.elia.lpdeck.gui.listener.SimpleMouseListener;
import me.elia.lpdeck.launchpad.LaunchpadLightListener;
import net.thecodersbreakfast.lp4j.api.Pad;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

@Getter
public class PadPanel extends JPanel implements LaunchpadLightListener {
    private final int padX;
    private final int padY;
    private final boolean edge;
    private final JFrame frame;

    public PadPanel(int padX, int padY, JFrame frame) {
        super();

        this.padX = padX;
        this.padY = padY;
        this.edge = this.padX == 8 || this.padY == 0;
        this.frame = frame;

        Lpdeck.getInstance().getLaunchpadClient().addLightListener(this);
    }

    public net.thecodersbreakfast.lp4j.api.Button getAsButton() {
        if (!this.edge) {
            return null;
        }
        return this.padX == 8
               ? net.thecodersbreakfast.lp4j.api.Button.atRight(this.padY - 1)
               : net.thecodersbreakfast.lp4j.api.Button.atTop(this.padX);
    }

    public Pad getAsPad() {
        return this.edge ? null : Pad.at(PadPanel.this.padX, PadPanel.this.padY - 1);
    }

    public void init() {
        this.setForeground(Color.GRAY);
        this.setFocusable(true);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.addMouseListener(new SimpleMouseListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                PadPanel.this.requestFocusInWindow();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    ActionRegistry registry = Lpdeck.getInstance().getActionRegistry();
                    if (PadPanel.this.edge) {
                        registry.onButtonPressed(
                                PadPanel.this.getAsButton(),
                                System.currentTimeMillis()
                        );
                    } else {
                        registry.onPadPressed(PadPanel.this.getAsPad(), System.currentTimeMillis());
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    new PadContextMenu(PadPanel.this).show(PadPanel.this, e.getX(), e.getY());
                }
            }
        });

        if (this.padX == 8 && this.padY == 0) {
            this.setVisible(false);
        }
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
        if (point.x == this.padX && point.y == this.padY) {
            Color swingColor = new Color(color.getRed() * 255 / 3, color.getGreen() * 255 / 3, 0);
            if (color.equals(net.thecodersbreakfast.lp4j.api.Color.BLACK)) {
                swingColor = Color.GRAY;
            }
            this.setForeground(swingColor);
        }
    }

    public String getTypeName() {
        return this.edge ? "Manager" : "Action";
    }
}
