package me.elia.lpdeck;

import me.elia.lpdeck.gui.LpdeckFrame;
import me.elia.lpdeck.gui.themes.LpdeckTheme;

import javax.swing.*;
import java.util.Arrays;

public class Main {
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        Lpdeck deck = new Lpdeck();
        deck.start();

        if (Arrays.stream(args).noneMatch(v -> v.equalsIgnoreCase("-nogui"))) {
            LpdeckTheme.setup();
            JFrame frame = new LpdeckFrame();
            frame.setVisible(true);
        }

        deck.registerActions();
    }
}