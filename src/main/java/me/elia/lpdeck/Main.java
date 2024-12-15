package me.elia.lpdeck;

public class Main {
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        Lpdeck deck = new Lpdeck();
        deck.start();
    }

    static {
        System.setProperty("java.awt.headless", "true");
    }
}