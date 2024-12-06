package me.elia.lpdeck;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class SpotifyIntegration extends WebSocketServer {
    private static final Logger LOGGER = LogManager.getLogger("Spotify WS");

    public SpotifyIntegration() {
        super(new InetSocketAddress("127.0.0.1", 7542));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        LOGGER.info("New client connected to Spotify WS");
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        LOGGER.info("Client disconnected from Spotify WS: {}", s.isEmpty() ? "No reason provided" : s);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {}

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        LOGGER.error("Error in Spotify WS", e);
    }

    @Override
    public void onStart() {
        LOGGER.info("Started Spotify WS");
    }
}
