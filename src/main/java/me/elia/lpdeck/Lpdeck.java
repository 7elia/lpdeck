package me.elia.lpdeck;

import lombok.Getter;
import me.elia.lpdeck.action.SpotifyPatchAction;
import me.elia.lpdeck.action.base.ActionRegistry;
import me.elia.lpdeck.action.CommandAction;
import me.elia.lpdeck.action.ServerTargetManager;
import me.elia.lpdeck.action.SyncedToggleAction;
import me.elia.lpdeck.action.DiscordPatchAction;
import me.elia.lpdeck.action.WebSocketManager;
import me.elia.lpdeck.action.voicemeeter.*;
import me.elia.lpdeck.launchpad.DeviceDetector;
import me.elia.lpdeck.launchpad.ListenableMidiLaunchpad;
import me.elia.lpdeck.launchpad.ListenableMidiLaunchpadClient;
import me.elia.lpdeck.patch.DiscordPatcher;
import me.elia.lpdeck.patch.SpotifyPatcher;
import me.elia.lpdeck.server.LpdeckServer;
import me.elia.lpdeck.server.ServerTarget;
import me.elia.lpdeck.voicemeeter.VoicemeeterIntegration;
import me.mattco.voicemeeter.Voicemeeter;
import net.thecodersbreakfast.lp4j.api.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.sound.midi.MidiUnavailableException;
import java.io.Closeable;
import java.io.IOException;

public class Lpdeck implements Closeable {
    private static Lpdeck INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger("Lpdeck");
    private final Launchpad launchpad;
    @Getter private final ListenableMidiLaunchpadClient launchpadClient;
    @Getter private final ActionRegistry actionRegistry;
    @Getter private final VoicemeeterIntegration voicemeeter;
    @Getter private final LpdeckServer server;

    public Lpdeck() {
        INSTANCE = this;

        LOGGER.info("Starting client...");

        this.actionRegistry = new ActionRegistry();
        try {
            this.launchpad = new ListenableMidiLaunchpad(DeviceDetector.detectDevices());
        } catch (MidiUnavailableException ignored) {
            throw new RuntimeException("Couldn't find input & output devices");
        }
        this.launchpad.setListener(this.actionRegistry);
        this.launchpadClient = (ListenableMidiLaunchpadClient) this.launchpad.getClient();

        this.server = new LpdeckServer();
        this.server.start();

        this.voicemeeter = new VoicemeeterIntegration();
        this.voicemeeter.start();

        SpotifyPatcher.patch(false);
        DiscordPatcher.patch();
    }

    public static @NotNull Lpdeck getInstance() {
        return INSTANCE;
    }

    public void registerActions() {
        this.actionRegistry.addManager(new WebSocketManager(7));

        this.actionRegistry.addManager(new ServerTargetManager(0, ServerTarget.SPOTIFY));
        this.actionRegistry.addAction(new SyncedToggleAction(0, 0, ServerTarget.SPOTIFY, "toggle_play", "playing"));
        this.actionRegistry.addAction(new SyncedToggleAction(0, 1, ServerTarget.SPOTIFY, "toggle_repeat", "repeat"));
        this.actionRegistry.addAction(new SyncedToggleAction(0, 2, ServerTarget.SPOTIFY, "toggle_shuffle", "shuffle"));
        this.actionRegistry.addAction(new CommandAction(0, 3, ServerTarget.SPOTIFY, "previous"));
        this.actionRegistry.addAction(new CommandAction(0, 4, ServerTarget.SPOTIFY, "next"));
        this.actionRegistry.addAction(new CommandAction(0, 5, ServerTarget.SPOTIFY, "lower_volume"));
        this.actionRegistry.addAction(new CommandAction(0, 6, ServerTarget.SPOTIFY, "increase_volume"));
        this.actionRegistry.addAction(new SpotifyPatchAction(0, 7));

        this.actionRegistry.addManager(new VoicemeeterManager(1));
        this.actionRegistry.addAction(new ToggleMuteAction(1, 0));
        this.actionRegistry.addAction(new ToggleMicAction(1, 1));
        this.actionRegistry.addAction(new ToggleAuxStreamAction(1, 2));
        this.actionRegistry.addAction(new ToggleLoopbackAction(1, 3));
        this.actionRegistry.addAction(new ToggleSpeakersAction(1, 4));

        this.actionRegistry.addManager(new ServerTargetManager(2, ServerTarget.DISCORD));
        this.actionRegistry.addAction(new SyncedToggleAction(2, 0, ServerTarget.DISCORD, "disconnect", "connected"));
        this.actionRegistry.addAction(new SyncedToggleAction(2, 1, ServerTarget.DISCORD, "toggle_deafen", "deafened"));
        this.actionRegistry.addAction(new SyncedToggleAction(2, 2, ServerTarget.DISCORD, "toggle_krisp", "krisp"));
        this.actionRegistry.addAction(new SyncedToggleAction(2, 3, ServerTarget.DISCORD, "toggle_screenshare", "screensharing"));
        this.actionRegistry.addAction(new SyncedToggleAction(2, 4, ServerTarget.DISCORD, "toggle_streamer_mode", "streamer_mode"));
        this.actionRegistry.addAction(new DiscordPatchAction(2, 5));
    }

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close, "Shutdown Thread"));
    }

    @Override
    public void close() {
        LOGGER.info("Closing client...");

        this.launchpadClient.reset();
        try {
            this.launchpad.close();
        } catch (IOException e) {
            LOGGER.error("Error while closing launchpad", e);
        }

        this.server.stop();

        Voicemeeter.logout();
    }
}
