package me.elia.lpdeck;

import lombok.Getter;
import me.elia.lpdeck.action.ActionRegistry;
import me.elia.lpdeck.action.CommandAction;
import me.elia.lpdeck.action.discord.DebugAction;
import me.elia.lpdeck.action.other.ServerManager;
import me.elia.lpdeck.action.spotify.*;
import me.elia.lpdeck.action.voicemeeter.*;
import me.elia.lpdeck.server.LpdeckServer;
import me.elia.lpdeck.server.ServerTarget;
import me.elia.lpdeck.voicemeeter.VoicemeeterIntegration;
import me.mattco.voicemeeter.Voicemeeter;
import net.thecodersbreakfast.lp4j.api.*;
import net.thecodersbreakfast.lp4j.midi.MidiLaunchpad;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.sound.midi.MidiUnavailableException;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class Lpdeck implements Closeable {
    private static Lpdeck INSTANCE;
    private static final CountDownLatch LATCH = new CountDownLatch(1);
    private static final Logger LOGGER = LogManager.getLogger("Lpdeck");
    private final Launchpad launchpad;
    @Getter private final LaunchpadClient launchpadClient;
    private final ActionRegistry actionRegistry;
    @Getter private final VoicemeeterIntegration voicemeeter;
    @Getter private final LpdeckServer server;

    public Lpdeck() {
        INSTANCE = this;

        LOGGER.info("Starting client");

        this.actionRegistry = new ActionRegistry();
        try {
            this.launchpad = new MidiLaunchpad(DeviceDetector.detectDevices());
        } catch (MidiUnavailableException ignored) {
            throw new RuntimeException("Couldn't find input & output devices");
        }
        this.launchpad.setListener(this.actionRegistry);
        this.launchpadClient = this.launchpad.getClient();

        this.server = new LpdeckServer();
        this.server.start();

        this.voicemeeter = new VoicemeeterIntegration();
        this.voicemeeter.start();

        this.registerActions();
    }

    public static @NotNull Lpdeck getInstance() {
        return INSTANCE;
    }

    private void registerActions() {
        this.actionRegistry.addManager(new ServerManager(7));

        this.actionRegistry.addManager(new SpotifyManager(0));
        this.actionRegistry.addAction(new TogglePlayAction(0, 0));
        this.actionRegistry.addAction(new ToggleRepeatAction(0, 1));
        this.actionRegistry.addAction(new ToggleShuffleAction(0, 2));
        this.actionRegistry.addAction(new CommandAction(0, 3, ServerTarget.SPOTIFY, "previous"));
        this.actionRegistry.addAction(new CommandAction(0, 4, ServerTarget.SPOTIFY, "next"));

        this.actionRegistry.addManager(new VoicemeeterManager(1));
        this.actionRegistry.addAction(new ToggleMuteAction(1, 0));
        this.actionRegistry.addAction(new ToggleMicAction(1, 1));
        this.actionRegistry.addAction(new ToggleAuxStreamAction(1, 2));
        this.actionRegistry.addAction(new ToggleLoopbackAction(1, 3));
        this.actionRegistry.addAction(new ToggleSpeakersAction(1, 4));

        this.actionRegistry.addAction(new DebugAction(2, 0));
    }

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close, "Shutdown Thread"));

        try {
            LATCH.await();
        } catch (InterruptedException e) {
            LOGGER.warn("Interrupted while awaiting latch, program will shut down...");
        }
    }

    public void close() {
        LOGGER.info("Closing client");

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
