package me.elia.lpdeck.action.base;

import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import me.elia.lpdeck.Lpdeck;
import me.elia.lpdeck.action.*;
import me.elia.lpdeck.action.voicemeeter.*;
import me.elia.lpdeck.patch.DiscordPatcher;
import me.elia.lpdeck.patch.SpotifyPatcher;
import me.elia.lpdeck.server.ServerTarget;
import net.thecodersbreakfast.lp4j.api.*;
import net.thecodersbreakfast.lp4j.api.Button;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ActionRegistry implements LaunchpadListener {
    private static final Logger LOGGER = LogManager.getLogger("Action Registry");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final List<Action> ACTIONS = new ArrayList<>();
    public static final List<Manager> MANAGERS = new ArrayList<>();

    public void register() {
        this.add(new SyncedToggleAction("toggle_play", ActionCategory.SPOTIFY, ServerTarget.SPOTIFY, "playing"));
        this.add(new SyncedToggleAction("toggle_repeat", ActionCategory.SPOTIFY, ServerTarget.SPOTIFY, "repeat"));
        this.add(new SyncedToggleAction("toggle_shuffle", ActionCategory.SPOTIFY, ServerTarget.SPOTIFY, "shuffle"));
        this.add(new CommandAction("previous", ActionCategory.SPOTIFY, ServerTarget.SPOTIFY));
        this.add(new CommandAction("next", ActionCategory.SPOTIFY, ServerTarget.SPOTIFY));
        this.add(new CommandAction("lower_volume", ActionCategory.SPOTIFY, ServerTarget.SPOTIFY));
        this.add(new CommandAction("increase_volume", ActionCategory.SPOTIFY, ServerTarget.SPOTIFY));
        this.add(new SpotifyPatchAction());
        this.add(new ToggleMuteAction());
        this.add(new SwapMicAction());
        this.add(new ToggleAuxStreamAction());
        this.add(new ToggleLoopbackAction());
        this.add(new ToggleSpeakersAction());
        this.add(new SyncedToggleAction("disconnect", ActionCategory.DISCORD, ServerTarget.DISCORD, "connected"));
        this.add(new SyncedToggleAction("toggle_deafen", ActionCategory.DISCORD, ServerTarget.DISCORD, "deafened"));
        this.add(new SyncedToggleAction("toggle_krisp", ActionCategory.DISCORD, ServerTarget.DISCORD, "krisp"));
        this.add(new SyncedToggleAction("toggle_screenshare", ActionCategory.DISCORD, ServerTarget.DISCORD, "screensharing"));
        this.add(new SyncedToggleAction("toggle_camera", ActionCategory.DISCORD, ServerTarget.DISCORD, "camera"));
        this.add(new SyncedToggleAction("toggle_streamer_mode", ActionCategory.DISCORD, ServerTarget.DISCORD, "streamer_mode"));

        this.add(new ServerTargetManager(ActionCategory.SPOTIFY, ServerTarget.SPOTIFY, () -> SpotifyPatcher.patch(true)));
        this.add(new VoicemeeterManager());
        this.add(new ServerTargetManager(ActionCategory.DISCORD, ServerTarget.DISCORD, () -> DiscordPatcher.patch(true)));
        this.add(new WebSocketManager());
    }

    private void add(Object obj) {
        if (obj instanceof Action action) {
            ACTIONS.add(action);
        } else if (obj instanceof Manager manager) {
            MANAGERS.add(manager);
        }
    }

    private Action findAction(String key) {
        for (Action action : ACTIONS) {
            if (action.getKey().equalsIgnoreCase(key)) {
                return action;
            }
        }
        return null;
    }

    private Manager findManager(String key) {
        for (Manager manager : MANAGERS) {
            if (manager.getCategory().name().equalsIgnoreCase(key)) {
                return manager;
            }
        }
        return null;
    }

    private JsonObject readRoamingObject(String fileName) throws IOException {
        File file = Lpdeck.getInstance().getRoamingFile(fileName);
        if (!file.exists()) {
            return new JsonObject();
        }
        String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        if (content.isEmpty()) {
            return new JsonObject();
        }
        JsonElement element = JsonParser.parseString(content);
        if (!element.isJsonObject()) {
            return new JsonObject();
        }
        return element.getAsJsonObject();
    }

    private void loadActions() throws IOException {
        JsonObject object = this.readRoamingObject("actions.json");
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            Action action = this.findAction(entry.getKey());
            if (action == null) {
                LOGGER.warn("Action of key '{}' not found, skipping.", entry.getKey());
                continue;
            }
            JsonObject actionConfig = entry.getValue().getAsJsonObject();
            action.setPos(new Point(actionConfig.get("x").getAsInt(), actionConfig.get("y").getAsInt()));
            LOGGER.info("Loaded action '{}' from config.", action.getKey());
        }
    }

    private void loadManagers() throws IOException {
        JsonObject object = this.readRoamingObject("managers.json");
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            Manager manager = this.findManager(entry.getKey());
            if (manager == null) {
                LOGGER.warn("Manager of key '{}' not found, skipping.", entry.getKey());
                continue;
            }
            JsonObject managerConfig = entry.getValue().getAsJsonObject();
            int pos = managerConfig.get("pos").getAsInt();
            manager.setPos(managerConfig.get("top").getAsBoolean() ? Button.atTop(pos) : Button.atRight(pos));
            LOGGER.info("Loaded manager for category '{}' from config.", manager.getCategory().name());
        }
    }

    public void load() {
        try {
            this.loadActions();
            this.loadManagers();
        } catch (IOException e) {
            LOGGER.error("Error while loading config", e);
        }
    }

    private void saveActions() throws IOException {
        File actionsFile = Lpdeck.getInstance().getRoamingFile("actions.json");
        JsonObject object = new JsonObject();
        for (Action action : ACTIONS) {
            if (action.getPos() == null) {
                continue;
            }
            JsonObject actionConfig = new JsonObject();
            actionConfig.addProperty("x", action.getPos().getX());
            actionConfig.addProperty("y", action.getPos().getY());
            object.add(action.getKey(), actionConfig);
        }
        FileUtils.writeStringToFile(actionsFile, GSON.toJson(object), StandardCharsets.UTF_8);
        LOGGER.info("Saved actions config.");
    }

    private void saveManagers() throws IOException {
        File managersFile = Lpdeck.getInstance().getRoamingFile("managers.json");
        JsonObject object = new JsonObject();
        for (Manager manager : MANAGERS) {
            if (manager.getPos() == null) {
                continue;
            }
            JsonObject managerConfig = new JsonObject();
            managerConfig.addProperty("top", manager.getPos().isTopButton());
            managerConfig.addProperty("pos", manager.getPos().getCoordinate());
            object.add(manager.getCategory().name().toLowerCase(), managerConfig);
        }
        FileUtils.writeStringToFile(managersFile, GSON.toJson(object), StandardCharsets.UTF_8);
        LOGGER.info("Saved managers config.");
    }

    public void save() {
        try {
            this.saveActions();
            this.saveManagers();
        } catch (IOException e) {
            LOGGER.error("Error while saving config", e);
        }
    }

    @Nullable
    public Action getActionAt(Pad pad) {
        return this.getActionAt(pad.getX(), pad.getY());
    }

    @Nullable
    public Action getActionAt(int x, int y) {
        return this.getActionAt(new Point(x, y));
    }

    @Nullable
    public Action getActionAt(Point pos) {
        for (Action action : ACTIONS) {
            if (pos.equals(action.getPos())) {
                return action;
            }
        }
        return null;
    }

    @Nullable
    public Manager getManagerAt(Button pos) {
        for (Manager manager : MANAGERS) {
            if (pos.equals(manager.getPos())) {
                return manager;
            }
        }
        return null;
    }

    @Override
    public void onPadPressed(Pad pad, long l) {
        Action action = this.getActionAt(pad.getX(), pad.getY());
        if (action != null) {
            action.onPress();
            LOGGER.info("Activated pad action '{}' at ({}, {}).", action.getKey(), pad.getX(), pad.getY());
            return;
        }
        LOGGER.info("No action registered for pad ({}, {}).", pad.getX(), pad.getY());
    }

    @Override
    public void onPadReleased(Pad pad, long l) { }

    @Override
    public void onButtonPressed(Button button, long l) {
        Manager manager = this.getManagerAt(button);
        if (manager != null) {
            manager.onPress();
            LOGGER.info("Activated manager '{}' at {} ({}).", manager.getCategory().name(), button.getCoordinate(), button.isTopButton() ? "top" : "right");
            return;
        }
        LOGGER.info("No manager registered at {} ({}).", button.getCoordinate(), button.isTopButton() ? "top" : "right");
    }

    @Override
    public void onButtonReleased(Button button, long l) { }

    @Override
    public void onTextScrolled(long l) { }
}
