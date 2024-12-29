package me.elia.lpdeck.voicemeeter;

public interface VoicemeeterListener {
    default void onMicChanged(boolean main) {}
    default void onConnected() {}
    default void onDisconnected() {}
}
