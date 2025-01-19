package me.elia.lpdeck.gui;

import me.elia.lpdeck.gui.appenders.GuiAppender;
import me.elia.lpdeck.gui.listener.SimpleDocumentListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.Objects;

public class LpdeckFrame extends JFrame {
    public static final Color BACKGROUND = new Color(29, 29, 29);

    public LpdeckFrame() {
        super("Lpdeck");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 600);
        this.setResizable(true);
        this.setBackground(BACKGROUND);

        this.setLayout(new BorderLayout());
        this.add(this.createLaunchpad(), BorderLayout.CENTER);
        this.add(this.createConsole(), BorderLayout.EAST);
    }

    private JPanel createLaunchpad() {
        JPanel launchpadPanel = new JPanel(new BorderLayout());

        launchpadPanel.add(this.createGrid(), BorderLayout.CENTER);
        launchpadPanel.add(this.createLogos(), BorderLayout.SOUTH);

        return launchpadPanel;
    }

    private JScrollPane createConsole() {
        JTextArea textArea = new JTextArea();
        textArea.setColumns((int) (this.getWidth() * 0.4) / textArea.getFontMetrics(textArea.getFont()).charWidth('M'));
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        ((DefaultCaret) textArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        textArea.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
        });

        GuiAppender appender = (GuiAppender) ((Logger) LogManager.getRootLogger()).getAppenders().get("Gui");
        appender.setConsoleArea(textArea);

        return new JScrollPane(textArea);
    }

    private JPanel createGrid() {
        JPanel gridPanel = new JPanel(new GridLayout(9, 9, 10, 10));
        gridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                PadPanel pad = new PadPanel(x, y, this);
                pad.init();
                gridPanel.add(pad);
                gridPanel.revalidate();
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
