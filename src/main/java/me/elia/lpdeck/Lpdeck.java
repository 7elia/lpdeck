package me.elia.lpdeck;

import lombok.Getter;
import me.elia.lpdeck.action.ActionRegistry;
import me.elia.lpdeck.action.spotify.*;
import me.elia.lpdeck.action.voicemeeter.*;
import me.elia.lpdeck.spotify.SpotifyIntegration;
import me.elia.lpdeck.spotify.SpotifyServerCommand;
import me.elia.lpdeck.voicemeeter.VoicemeeterIntegration;
import me.mattco.voicemeeter.Voicemeeter;
import me.mattco.voicemeeter.VoicemeeterException;
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
    @Getter private final SpotifyIntegration spotify;
    @Getter private final VoicemeeterIntegration voicemeeter;

    public Lpdeck() throws Exception {
        INSTANCE = this;

        LOGGER.info("Starting client");

        this.actionRegistry = new ActionRegistry();
        this.launchpad = new MidiLaunchpad(DeviceDetector.detectDevices());
        this.launchpad.setListener(this.actionRegistry);
        this.launchpadClient = this.launchpad.getClient();

        this.spotify = new SpotifyIntegration();
        this.spotify.startServer();

        this.voicemeeter = new VoicemeeterIntegration();
        this.voicemeeter.start();

        this.registerActions();
    }

    public static @NotNull Lpdeck getInstance() {
        return INSTANCE;
    }

    private void registerActions() {
        this.actionRegistry.addManager(new SpotifyManager(0));
        this.actionRegistry.addAction(new TogglePlayAction(0, 0));
        this.actionRegistry.addAction(new ToggleRepeatAction(0, 1));
        this.actionRegistry.addAction(new ToggleShuffleAction(0, 2));
        this.actionRegistry.addAction(new SpotifyCommandAction(0, 3, SpotifyServerCommand.PREVIOUS));
        this.actionRegistry.addAction(new SpotifyCommandAction(0, 4, SpotifyServerCommand.NEXT));

        this.actionRegistry.addManager(new VoicemeeterManager(1));
        this.actionRegistry.addAction(new ToggleMuteAction(1, 0));
        this.actionRegistry.addAction(new ToggleMicAction(1, 1));
        this.actionRegistry.addAction(new ToggleAuxStreamAction(1, 2));
        this.actionRegistry.addAction(new ToggleLoopbackAction(1, 3));
        this.actionRegistry.addAction(new ToggleSpeakersAction(1, 4));
    }

    public void start() throws InterruptedException {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close, "Shutdown Thread"));

        LATCH.await();
    }

    public void close() {
        LOGGER.info("Closing client");

        this.launchpadClient.reset();
        try {
            this.launchpad.close();
        } catch (IOException e) {
            LOGGER.error("Error while closing launchpad", e);
        }
        try {
            this.spotify.stopServer();
        } catch (InterruptedException e) {
            LOGGER.error("Error while closing Spotify WS", e);
        }
        Voicemeeter.logout();
    }
}
