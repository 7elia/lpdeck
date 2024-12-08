package me.elia.lpdeck.spotify;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class SpotifyIntegration {
    @Getter private final List<SpotifyListener> listeners;
    private SpotifyServer server;
    private boolean startedServer;

    public SpotifyIntegration() {
        this.listeners = new ArrayList<>();
        this.server = new SpotifyServer(this::getListeners);
        this.startedServer = false;
    }

    public void addListener(SpotifyListener listener) {
        this.listeners.add(listener);
    }

    public void startServer() {
        if (this.startedServer) {
            return;
        }
        this.server.start();
        this.startedServer = true;
    }

    public void stopServer() throws InterruptedException {
        if (!this.startedServer) {
            return;
        }
        this.server.stop();
        this.startedServer = false;
    }

    public void restartServer() throws InterruptedException {
        if (!this.startedServer) {
            return;
        }
        this.stopServer();
        this.server = new SpotifyServer(this::getListeners);
        this.startServer();
    }

    public SpotifyState getState() {
        return this.server.getState();
    }

    public void sendCommand(SpotifyServerCommand command) {
        this.server.broadcast(command.name().toLowerCase());
    }

    public boolean hasConnectedClients() {
        return !this.server.getConnections().isEmpty();
    }
}
