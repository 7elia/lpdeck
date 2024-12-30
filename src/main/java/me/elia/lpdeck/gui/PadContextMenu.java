package me.elia.lpdeck.gui;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import me.elia.lpdeck.Lpdeck;
import me.elia.lpdeck.action.base.Action;
import me.elia.lpdeck.action.base.ActionRegistry;
import me.elia.lpdeck.action.base.Manager;

import javax.swing.*;
import java.awt.*;

public class PadContextMenu extends JPopupMenu {
    private final PadPanel pad;

    public PadContextMenu(PadPanel pad) {
        this.pad = pad;

        {
            JMenuItem item = new JMenuItem("Set Action", IconFontSwing.buildIcon(FontAwesome.UPLOAD, 16, Color.WHITE));
            item.addActionListener(e -> {
                System.out.println("AAAAAAAA");
            });
            this.add(item);
        }
        {
            JMenuItem item = new JMenuItem("Clear Action", IconFontSwing.buildIcon(FontAwesome.TRASH_O, 16, Color.WHITE));
            item.addActionListener(e -> {
                if (this.pad.isEdge()) {
                    Manager manager;
                    if ((manager = this.getManager()) != null) {
                        manager.setPos(null);
                    }
                } else {
                    Action action;
                    if ((action = this.getAction()) != null) {
                        action.setPos(null);
                    }
                }
                Lpdeck.getInstance().getActionRegistry().save();
            });
            this.add(item);
        }
    }

    private Manager getManager() {
        for (Manager manager : ActionRegistry.MANAGERS) {
            if (manager.getPos() != null && manager.getPos().getCoordinate() == (manager.getPos().isTopButton() ? this.pad.getPadX() : this.pad.getPadY() - 1)) {
                return manager;
            }
        }
        return null;
    }

    private Action getAction() {
        for (Action action : ActionRegistry.ACTIONS) {
            if (action.getPos() != null && action.getPos().getX() == this.pad.getPadX() && action.getPos().getY() == this.pad.getPadY() - 1) {
                return action;
            }
        }
        return null;
    }
}
