package me.elia.lpdeck.spotify;

import com.google.gson.Gson;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.function.Supplier;

public class SpotifyServer extends WebSocketServer {
    private static final Logger LOGGER = LogManager.getLogger("Spotify WS");
    private final Supplier<List<SpotifyListener>> listeners;
    @Getter private SpotifyState state;

    public SpotifyServer(Supplier<List<SpotifyListener>> listeners) {
        super(new InetSocketAddress("127.0.0.1", 7542));
        this.listeners = listeners;
        this.state = new SpotifyState(false, false, false);
    }

    private void resetState() {
        this.state = new SpotifyState(false, false, false);
        for (SpotifyListener listener : this.listeners.get()) {
            listener.updated(this.state);
            listener.disconnected(this.getConnections().size());
        }
    }

    @Override
    public final void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        for (SpotifyListener listener : this.listeners.get()) {
            listener.connected();
        }
        LOGGER.info("New client connected to Spotify WS");
    }

    @Override
    public final void onClose(WebSocket webSocket, int code, String reason, boolean b) {
        this.resetState();
        LOGGER.info("Client disconnected from Spotify WS: {}", reason.isEmpty() ? "No reason provided" : reason);
    }

    @Override
    public final void onMessage(WebSocket webSocket, String message) {
        this.state = new Gson().fromJson(message, SpotifyState.class);
        for (SpotifyListener listener : this.listeners.get()) {
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

    @Override
    public void stop(int timeout, String closeMessage) throws InterruptedException {
        this.resetState();
        super.stop(timeout, closeMessage);
    }
}
