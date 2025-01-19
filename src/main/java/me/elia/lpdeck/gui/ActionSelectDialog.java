package me.elia.lpdeck.gui;

import me.elia.lpdeck.Lpdeck;
import me.elia.lpdeck.action.base.Action;
import me.elia.lpdeck.action.base.ActionRegistry;
import me.elia.lpdeck.action.base.Manager;
import net.thecodersbreakfast.lp4j.api.Pad;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ActionSelectDialog extends JDialog {
    private final PadPanel pad;
    private JComboBox<?> dropdown;

    public ActionSelectDialog(PadPanel pad) {
        super(pad.getFrame(), "Set " + (pad.isEdge() ? "Manager" : "Action"));

        this.pad = pad;

        this.setLayout(new BorderLayout());
        this.setMinimumSize(new Dimension(400, 130));

        this.add(this.createPanel(), BorderLayout.NORTH);
        this.add(this.createButtons(), BorderLayout.SOUTH);
    }

    private JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Select " + (this.pad.isEdge() ? "Manager" : "Action") + ":"), BorderLayout.NORTH);

        this.dropdown = new JComboBox<>(this.pad.isEdge() ? ActionRegistry.MANAGERS.toArray(new Manager[0]) : ActionRegistry.ACTIONS.toArray(new Action[0]));
        panel.add(this.dropdown, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createButtons() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton confirmButton = new JButton("OK");
        confirmButton.addActionListener(e -> {
            if (this.dropdown.getSelectedItem() == null) {
                return;
            }
            if (this.pad.isEdge()) {
                ((Manager) this.dropdown.getSelectedItem()).setPos(this.pad.getAsButton());
            } else {
                Pad pad = this.pad.getAsPad();
                ((Action) this.dropdown.getSelectedItem()).setPos(new Point(pad.getX(), pad.getY()));
            }
            Lpdeck.getInstance().getActionRegistry().save();
            this.dispose();
        });
        buttonsPanel.add(confirmButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> this.dispose());
        buttonsPanel.add(cancelButton);

        return buttonsPanel;
    }
}
