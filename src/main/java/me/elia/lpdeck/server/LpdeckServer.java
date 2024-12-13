package me.elia.lpdeck.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class LpdeckServer {
    private static final Logger LOGGER = LogManager.getLogger("Lpdeck WS");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    @NotNull private WebSocketServerImpl websocket;

    public LpdeckServer() {
        this.websocket = new WebSocketServerImpl();
    }

    public void sendCommand(ServerTarget target, String command) {
        for (WebSocket client : this.websocket.getClientsFor(target)) {
            client.send(command.toLowerCase());
        }
    }

    private void onSync(ServerTarget target, JsonObject data) {
        for (ServerListener listener : target.getListeners()) {
            listener.onDataSync(data);
        }
    }

    public boolean hasClientsFor(ServerTarget target) {
        return !this.websocket.getClientsFor(target).isEmpty();
    }

    public void closeTarget(ServerTarget target) {
        LOGGER.info("Closing all clients for target {}", target);
        for (WebSocket client : this.websocket.getClientsFor(target)) {
            client.close();
        }
    }

    public boolean isRunning() {
        return this.websocket.isStarted();
    }

    public void start() {
        if (this.isRunning()) {
            return;
        }
        this.websocket.start();
    }

    public void stop() {
        if (!this.isRunning()) {
            return;
        }
        try {
            this.websocket.stop();
        } catch (InterruptedException e) {
            LOGGER.error("Error while stopping Lpdeck WS", e);
        }
    }

    public void restart() {
        this.stop();
        this.websocket = new WebSocketServerImpl();
        this.start();
    }

    @Getter
    private class WebSocketServerImpl extends WebSocketServer {
        private boolean started;

        public WebSocketServerImpl() {
            super(new InetSocketAddress("127.0.0.1", 7542));
        }

        public List<WebSocket> getClientsFor(ServerTarget target) {
            List<WebSocket> clients = new ArrayList<>();
            for (WebSocket client : this.getConnections()) {
                if (client.getAttachment() instanceof ServerTarget attachment) {
                    if (attachment.equals(target)) {
                        clients.add(client);
                    }
                }
            }
            return clients;
        }

        @Override
        public void onOpen(WebSocket client, ClientHandshake clientHandshake) {
            LOGGER.info("New connection opened to Lpdeck WS");
        }

        @Override
        public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {
            if (webSocket.getAttachment() instanceof ServerTarget attachment) {
                for (ServerListener listener : attachment.getListeners()) {
                    listener.onClientDisconnected(this.getClientsFor(attachment).size());
                }
            }
            LOGGER.info("Connection closed from Lpdeck WS");
        }

        @Override
        public void onMessage(WebSocket client, String s) {
            JsonObject message = JsonParser.parseString(s).getAsJsonObject();
            if (!message.has("type") || !message.has("target")) {
                return;
            }
            ServerTarget target = ServerTarget.of(message.get("target").getAsString());
            String messageType = message.get("type").getAsString().toLowerCase();
            switch (messageType) {
                case "sync":
                    if (message.has("data")) {
                        LpdeckServer.this.onSync(ServerTarget.of(message.get("target").getAsString()), message.get("data").getAsJsonObject());
                    }
                    break;
                case "target":
                    client.setAttachment(target);
                    for (ServerListener listener : target.getListeners()) {
                        listener.onClientConnected();
                    }
                    break;
            }
        }

        @Override
        public void onError(WebSocket webSocket, Exception e) {
            LOGGER.error("Error in Lpdeck WS", e);
        }

        @Override
        public void onStart() {
            this.started = true;
            LOGGER.info("Started Lpdeck WS");
        }

        @Override
        public void stop(int timeout, String closeMessage) throws InterruptedException {
            this.started = false;
            LOGGER.info("Stopping Lpdeck WS");
            super.stop(timeout, closeMessage);
        }
    }
}
