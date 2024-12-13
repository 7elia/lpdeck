package me.elia.lpdeck.server;

import com.google.gson.JsonObject;

public interface ServerListener {
    default void onDataSync(JsonObject data) {}
    default void onClientConnected() {}
    default void onClientDisconnected(int connections) {}
}
