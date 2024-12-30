package me.elia.lpdeck.gui.listener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public interface SimpleDocumentListener extends DocumentListener {
    @Override
    default void changedUpdate(DocumentEvent e) { }
    @Override
    default void insertUpdate(DocumentEvent e) { }
    @Override
    default void removeUpdate(DocumentEvent e) { }
}
