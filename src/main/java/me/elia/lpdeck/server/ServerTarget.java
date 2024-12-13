package me.elia.lpdeck.server;

import lombok.Getter;
import me.elia.lpdeck.Lpdeck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum ServerTarget {
    SPOTIFY,
    DISCORD;

    private final List<ServerListener> listeners;

    ServerTarget() {
        this.listeners = new ArrayList<>();
    }

    public void addListener(ServerListener listener) {
        this.listeners.add(listener);
    }

    public void sendCommand(String command) {
        Lpdeck.getInstance().getServer().sendCommand(this, command);
    }

    public static ServerTarget of(String id) {
        return Arrays.stream(values()).filter(v -> v.name().equalsIgnoreCase(id)).findFirst().orElse(null);
    }
}
