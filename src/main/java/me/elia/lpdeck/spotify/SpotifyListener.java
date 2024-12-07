package me.elia.lpdeck.spotify;

public interface SpotifyListener {
    default void updated(SpotifyState state) {}
    default void connected() {}
    default void disconnected(int connections) {}
}
