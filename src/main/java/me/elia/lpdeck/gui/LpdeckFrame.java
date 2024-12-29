package me.elia.lpdeck.gui;

import me.elia.lpdeck.launchpad.LaunchpadLightListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class LpdeckFrame extends JFrame implements LaunchpadLightListener {
    private static final Color BACKGROUND = new Color(29, 29, 29);

    public LpdeckFrame() {
        super("Lpdeck");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 600);
        this.setResizable(true);
        this.setBackground(BACKGROUND);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(this.createLaunchpad(), BorderLayout.CENTER);
        panel.add(this.createConsole(), BorderLayout.EAST);

        this.add(panel);
    }

    private JPanel createLaunchpad() {
        JPanel launchpadPanel = new JPanel(new BorderLayout());

        launchpadPanel.add(this.createGrid(), BorderLayout.CENTER);
        launchpadPanel.add(this.createLogos(), BorderLayout.SOUTH);

        return launchpadPanel;
    }

    private JScrollPane createConsole() {
        JTextArea textArea = new JTextArea();
        textArea.setPreferredSize(new Dimension((int) (this.getWidth() * 0.4F), this.getHeight()));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

//        ConsoleAppender appender = (ConsoleAppender) ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).getAppenders().get("Gui");
//        appender.setConsole(textArea);

        return scrollPane;
    }

    private JPanel createGrid() {
        JPanel gridPanel = new JPanel(new GridLayout(9, 9, 10, 10));
        gridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                JPanel pad = new PadPanel(x, y);
                gridPanel.add(pad);
            }
        }

        return gridPanel;
    }

    private JPanel createLogos() {
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BorderLayout());
        logoPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        logoPanel.setBackground(BACKGROUND);

        JLabel novationLogo = new JLabel(this.scaleImage(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("logos/novation.png"))), 30)) {
            @Override
            protected void paintComponent(Graphics g) {
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paintComponent(g);
            }
        };
        logoPanel.add(novationLogo, BorderLayout.WEST);
        logoPanel.add(new JLabel("LPDECK"), BorderLayout.EAST);

        return logoPanel;
    }

    public ImageIcon scaleImage(ImageIcon icon, int height) {
        int nw = icon.getIconWidth();
        int nh = icon.getIconHeight();

        if (nh > height) {
            nh = height;
            nw = (icon.getIconWidth() * nh) / icon.getIconHeight();
        }

        return new ImageIcon(icon.getImage().getScaledInstance(nw, nh, Image.SCALE_DEFAULT));
    }
}
