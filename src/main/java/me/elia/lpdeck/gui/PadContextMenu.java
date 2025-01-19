package me.elia.lpdeck.gui;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import me.elia.lpdeck.Lpdeck;
import me.elia.lpdeck.action.base.Action;
import me.elia.lpdeck.action.base.ActionRegistry;
import me.elia.lpdeck.action.base.Manager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PadContextMenu extends JPopupMenu {
    private final PadPanel pad;

    public PadContextMenu(PadPanel pad) {
        this.pad = pad;

        this.setBorder(new EmptyBorder(5, 0, 5, 0));

        String typeName = this.pad.isEdge() ? "Manager" : "Action";
        String active = null;
        if (this.pad.isEdge()) {
            for (Manager manager : ActionRegistry.MANAGERS) {
                if (manager.getPos().equals(this.pad.getAsButton())) {
                    active = manager.toString();
                    break;
                }
            }
        } else {
            for (Action action : ActionRegistry.ACTIONS) {
                if (action.getPos() == null) {
                    continue;
                }
                if (action.getPos().getX() == this.pad.getAsPad().getX() && action.getPos().getY() == this.pad.getAsPad().getY()) {
                    active = action.toString();
                    break;
                }
            }
        }
        if (active != null) {
            JLabel activeLabel = new JLabel(String.format("<html><b>Current %s:</b><br>%s</html", typeName, active));
            activeLabel.setBorder(new EmptyBorder(0, 5, 5, 0));
            this.add(activeLabel);
            this.add(new JSeparator());
        }
        {
            JMenuItem item = new JMenuItem("Set " + typeName, IconFontSwing.buildIcon(FontAwesome.UPLOAD, 16, Color.WHITE));
            item.addActionListener(e -> {
                JDialog dialog = new ActionSelectDialog(this.pad);
                dialog.setLocationRelativeTo(this.pad.getFrame());
                dialog.setVisible(true);
            });
            this.add(item);
        }
        {
            JMenuItem item = new JMenuItem("Clear " + typeName, IconFontSwing.buildIcon(FontAwesome.TRASH_O, 16, Color.WHITE));
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
