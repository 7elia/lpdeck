package me.elia.lpdeck;

import com.formdev.flatlaf.FlatDarculaLaf;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import me.elia.lpdeck.gui.LpdeckFrame;

import javax.swing.*;
import java.util.Arrays;

public class Main {
    public static boolean IS_DEVELOPMENT;

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        IS_DEVELOPMENT = Arrays.stream(args).anyMatch(v -> v.equalsIgnoreCase("-development"));

        Lpdeck deck = new Lpdeck();
        deck.start();

        if (Arrays.stream(args).noneMatch(v -> v.equalsIgnoreCase("-nogui"))) {
            FlatDarculaLaf.setup();
            IconFontSwing.register(FontAwesome.getIconFont());
            JFrame frame = new LpdeckFrame();
            frame.setVisible(true);
        }

        deck.getActionRegistry().load();
    }
}