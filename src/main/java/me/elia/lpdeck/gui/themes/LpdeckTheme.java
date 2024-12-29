package me.elia.lpdeck.gui.themes;

import com.formdev.flatlaf.FlatDarkLaf;

public class LpdeckTheme extends FlatDarkLaf {
    @SuppressWarnings("UnusedReturnValue")
    public static boolean setup() {
        return setup(new LpdeckTheme());
    }

    @Override
    public String getName() {
        return "LpdeckTheme";
    }
}
