package me.elia.lpdeck;

import com.formdev.flatlaf.FlatDarculaLaf;
import me.elia.lpdeck.gui.LpdeckFrame;

import javax.swing.*;
import java.util.Arrays;

public class Main {
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        Lpdeck deck = new Lpdeck();
        deck.start();

        if (Arrays.stream(args).noneMatch(v -> v.equalsIgnoreCase("-nogui"))) {
            FlatDarculaLaf.setup();
            JFrame frame = new LpdeckFrame();
            frame.setVisible(true);
        }

        deck.registerActions();
    }
}