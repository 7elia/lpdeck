package me.elia.lpdeck.spotify;

import com.google.gson.Gson;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class SpotifyServer extends WebSocketServer {
    private static final Logger LOGGER = LogManager.getLogger("Spotify WS");
    private final List<SpotifyListener> listeners;
    @Getter private SpotifyState state;

    public SpotifyServer() {
        super(new InetSocketAddress("127.0.0.1", 7542));
        this.listeners = new ArrayList<>();
        this.state = new SpotifyState(false, false, false);
    }

    public void addListener(SpotifyListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public final void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        for (SpotifyListener listener : this.listeners) {
            listener.connected();
        }
        LOGGER.info("New client connected to Spotify WS");
    }

    @Override
    public final void onClose(WebSocket webSocket, int code, String reason, boolean b) {
        this.state = new SpotifyState(false, false, false);
        for (SpotifyListener listener : this.listeners) {
            listener.updated(this.state);
            listener.disconnected(this.getConnections().size());
        }
        LOGGER.info("Client disconnected from Spotify WS: {}", reason.isEmpty() ? "No reason provided" : reason);
    }

    @Override
    public final void onMessage(WebSocket webSocket, String message) {
        this.state = new Gson().fromJson(message, SpotifyState.class);
        for (SpotifyListener listener : this.listeners) {
            listener.updated(this.state);
        }
    }

    @Override
    public final void onError(WebSocket webSocket, Exception e) {
        LOGGER.error("Error in Spotify WS", e);
    }

    @Override
    public final void onStart() {
        LOGGER.info("Started Spotify WS");
    }
}
