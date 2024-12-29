package me.elia.lpdeck.action.base;

import lombok.RequiredArgsConstructor;
import me.elia.lpdeck.action.*;
import me.elia.lpdeck.action.voicemeeter.*;
import me.elia.lpdeck.server.ServerTarget;
import net.thecodersbreakfast.lp4j.api.*;
import net.thecodersbreakfast.lp4j.api.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ActionRegistry implements LaunchpadListener {
    private static final Logger LOGGER = LogManager.getLogger("Action Registry");
    public static final List<Action> ACTIONS = new ArrayList<>();
    public static final Action TOGGLE_PLAY = register(new SyncedToggleAction("toggle_play", ActionCategory.SPOTIFY, ServerTarget.SPOTIFY, "playing"));
    public static final Action TOGGLE_REPEAT = register(new SyncedToggleAction("toggle_repeat", ActionCategory.SPOTIFY, ServerTarget.SPOTIFY, "repeat"));
    public static final Action TOGGLE_SHUFFLE = register(new SyncedToggleAction("toggle_shuffle", ActionCategory.SPOTIFY, ServerTarget.SPOTIFY, "shuffle"));
    public static final Action PREVIOUS = register(new CommandAction("previous", ActionCategory.SPOTIFY, ServerTarget.SPOTIFY));
    public static final Action NEXT = register(new CommandAction("next", ActionCategory.SPOTIFY, ServerTarget.SPOTIFY));
    public static final Action LOWER_VOLUME = register(new CommandAction("lower_volume", ActionCategory.SPOTIFY, ServerTarget.SPOTIFY));
    public static final Action INCREASE_VOLUME = register(new CommandAction("increase_volume", ActionCategory.SPOTIFY, ServerTarget.SPOTIFY));
    public static final Action PATCH_SPOTIFY = register(new SpotifyPatchAction());
    public static final Action TOGGLE_MUTE = register(new ToggleMuteAction());
    public static final Action SWAP_MIC = register(new SwapMicAction());
    public static final Action TOGGLE_AUX_STREAM = register(new ToggleAuxStreamAction());
    public static final Action TOGGLE_LOOPBACK = register(new ToggleLoopbackAction());
    public static final Action TOGGLE_SPEAKERS = register(new ToggleSpeakersAction());
    public static final Action DISCONNECT = register(new SyncedToggleAction("disconnect", ActionCategory.DISCORD, ServerTarget.DISCORD, "connected"));
    public static final Action TOGGLE_DEAFEN = register(new SyncedToggleAction("toggle_deafen", ActionCategory.DISCORD, ServerTarget.DISCORD, "deafened"));
    public static final Action TOGGLE_KRISP = register(new SyncedToggleAction("toggle_krisp", ActionCategory.DISCORD, ServerTarget.DISCORD, "krisp"));
    public static final Action TOGGLE_SCREENSHARE = register(new SyncedToggleAction("toggle_screenshare", ActionCategory.DISCORD, ServerTarget.DISCORD, "screensharing"));
    public static final Action TOGGLE_STREAMER_MODE = register(new SyncedToggleAction("toggle_streamer_mode", ActionCategory.DISCORD, ServerTarget.DISCORD, "streamer_mode"));
    public static final Action PATCH_DISCORD = register(new DiscordPatchAction());
    public static final List<Manager> MANAGERS = new ArrayList<>();
    public static final Manager SPOTIFY_MANAGER = registerManager(new ServerTargetManager(ActionCategory.SPOTIFY, ServerTarget.SPOTIFY));
    public static final Manager VOICEMEETER_MANAGER = registerManager(new VoicemeeterManager());
    public static final Manager DISCORD_MANAGER = registerManager(new ServerTargetManager(ActionCategory.DISCORD, ServerTarget.DISCORD));
    public static final Manager WEBSOCKET_MANAGER = registerManager(new WebSocketManager());

    private static Action register(Action action) {
        ACTIONS.add(action);
        return action;
    }

    private static Manager registerManager(Manager manager) {
        MANAGERS.add(manager);
        return manager;
    }

    @Override
    public void onPadPressed(Pad pad, long l) {
        for (Action action : ACTIONS) {
            if (action.getPos() != null && action.getPos().x == pad.getX() && action.getPos().x == pad.getY()) {
                action.press();
                LOGGER.info("Activated pad action at ({}, {}).", pad.getX(), pad.getY());
                return;
            }
        }
        LOGGER.info("No action registered for pad ({}, {}).", pad.getX(), pad.getY());
    }

    @Override
    public void onPadReleased(Pad pad, long l) { }

    @Override
    public void onButtonPressed(Button button, long l) {
        for (Manager manager : MANAGERS) {
            if (manager.getPos().equals(button)) {
                manager.onPressed();
            }
        }
    }

    @Override
    public void onButtonReleased(Button button, long l) { }

    @Override
    public void onTextScrolled(long l) { }
}
