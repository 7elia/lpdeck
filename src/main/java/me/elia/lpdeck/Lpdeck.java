package me.elia.lpdeck;

import me.elia.lpdeck.action.ActionRegistry;
import me.elia.lpdeck.action.ButtonAction;
import me.elia.lpdeck.action.PadAction;
import me.mattco.voicemeeter.Voicemeeter;
import me.mattco.voicemeeter.VoicemeeterException;
import net.thecodersbreakfast.lp4j.api.*;
import net.thecodersbreakfast.lp4j.midi.MidiLaunchpad;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class Lpdeck implements Closeable {
    private static final CountDownLatch LATCH = new CountDownLatch(1);
    private static final Logger LOGGER = LogManager.getLogger("Lpdeck");
    private final Launchpad launchpad;
    private final LaunchpadClient client;
    private final ActionRegistry actionRegistry;
    @NotNull private SpotifyIntegration spotify;
    private boolean usingMainMic;
    private boolean busMuted;
    private boolean streamingAux;

    public Lpdeck() throws Exception {
        LOGGER.info("Starting client");

        this.launchpad = new MidiLaunchpad(DeviceDetector.detectDevices());
        this.client = this.launchpad.getClient();
        this.actionRegistry = new ActionRegistry(this.client);
        this.launchpad.setListener(this.actionRegistry);

        this.spotify = new SpotifyIntegration();
        this.spotify.start();

        Voicemeeter.init(System.getProperty("os.arch").contains("64"));
        Voicemeeter.login();

        this.busMuted = Voicemeeter.getParameterFloat("Bus[3].Mute") == 1.0F;
        this.usingMainMic = Voicemeeter.getParameterFloat("Strip[0].B1") == 1.0F;
        this.streamingAux = Voicemeeter.getParameterFloat("Strip[4].B1") == 1.0F;

        this.registerActions();

        this.actionRegistry.resetColors();
    }

    private void registerActions() {
        this.actionRegistry.addAction(new ButtonAction(Button.atTop(0), Color.RED, b -> {
            try {
                this.spotify.stop();
                this.spotify = new SpotifyIntegration();
                this.spotify.start();
            } catch (InterruptedException e) {
                LOGGER.error("Couldn't restart Spotify WS", e);
            }
        }));
        this.actionRegistry.addAction(new ButtonAction(Button.atTop(1), Color.RED, b -> {
            try {
                Voicemeeter.logout();
                Voicemeeter.init(System.getProperty("os.arch").contains("64"));
                Voicemeeter.login();
                LOGGER.info("Restarted Voicemeeter instance");
            } catch (VoicemeeterException e) {
                LOGGER.error("Couldn't restart Voicemeeter instance", e);
            }
        }));

        this.actionRegistry.addAction(new PadAction(Pad.at(0, 0), Color.GREEN, p -> this.spotify.broadcast("toggle_play")));
        this.actionRegistry.addAction(new PadAction(Pad.at(0, 1), Color.ORANGE, p -> this.spotify.broadcast("previous")));
        this.actionRegistry.addAction(new PadAction(Pad.at(0, 2), Color.YELLOW, p -> this.spotify.broadcast("next")));

        this.actionRegistry.addAction(new PadAction(Pad.at(1, 0), Color.RED, p -> {
            Voicemeeter.setParameterFloat("Bus[3].Mute", this.busMuted ? 0.0F : 1.0F);
            this.busMuted = !this.busMuted;
        }));
        this.actionRegistry.addAction(new PadAction(Pad.at(1, 1), Color.ORANGE, p -> {
            Voicemeeter.setParameterFloat("Strip[0].B1", this.usingMainMic ? 0.0F : 1.0F);
            Voicemeeter.setParameterFloat("Strip[1].B1", this.usingMainMic ? 1.0F : 0.0F);
            this.usingMainMic = !this.usingMainMic;
        }));
        this.actionRegistry.addAction(new PadAction(Pad.at(1, 2), Color.ORANGE, p -> {
            Voicemeeter.setParameterFloat("Strip[4].B1", this.streamingAux ? 0.0F : 1.0F);
            this.streamingAux = !this.streamingAux;
        }));
    }

    public void start() throws InterruptedException {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close, "Shutdown Thread"));

        LATCH.await();
    }

    public void close() {
        LOGGER.info("Closing client");

        this.client.reset();
        try {
            this.launchpad.close();
        } catch (IOException e) {
            LOGGER.error("Error while closing launchpad", e);
        }
        try {
            this.spotify.stop();
        } catch (InterruptedException e) {
            LOGGER.error("Error while closing Spotify WS", e);
        }
        Voicemeeter.logout();
    }
}
