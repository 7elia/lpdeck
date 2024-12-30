package me.elia.lpdeck.gui;

import javax.swing.*;

public class PadContextMenu extends JPopupMenu {
    public PadContextMenu(PadPanel pad) {
        {
            JMenuItem item = new JMenuItem("Set Action");
            item.addActionListener(e -> {
                System.out.println("AAAAAAAA");
            });
            this.add(item);
        }
        {
            JMenuItem item = new JMenuItem("Clear Action");
            item.addActionListener(e -> {
                System.out.println("BBBBBBBBBBBBBBBBBB");
            });
            this.add(item);
        }
    }
}
