package me.elia.lpdeck.voicemeeter;

public interface VoicemeeterListener {
    default void micChanged(boolean main) {}
    default void connected() {}
    default void disconnected() {}
}
