package me.elia.lpdeck;

import lombok.Getter;
import me.elia.lpdeck.action.base.ActionRegistry;
import me.elia.lpdeck.launchpad.DeviceDetector;
import me.elia.lpdeck.launchpad.ListenableMidiLaunchpad;
import me.elia.lpdeck.launchpad.ListenableMidiLaunchpadClient;
import me.elia.lpdeck.patch.DiscordPatcher;
import me.elia.lpdeck.patch.SpotifyPatcher;
import me.elia.lpdeck.server.LpdeckServer;
import me.elia.lpdeck.voicemeeter.VoicemeeterIntegration;
import me.mattco.voicemeeter.Voicemeeter;
import net.thecodersbreakfast.lp4j.api.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.sound.midi.MidiUnavailableException;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Lpdeck implements Closeable {
    private static Lpdeck INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger("Lpdeck");
    private final Path roamingPath;
    @Getter private final ActionRegistry actionRegistry;
    private final Launchpad launchpad;
    @Getter private final ListenableMidiLaunchpadClient launchpadClient;
    @Getter private final VoicemeeterIntegration voicemeeter;
    @Getter private final LpdeckServer server;

    public Lpdeck() {
        INSTANCE = this;

        LOGGER.info("Starting client...");

        this.roamingPath = getRoamingPath();
        if (this.roamingPath == null) {
            throw new RuntimeException("OS not implemented!");
        }
        if (!this.roamingPath.toFile().exists() && !this.roamingPath.toFile().mkdirs()) {
            LOGGER.warn("Couldn't create AppData folder.");
        }

        this.actionRegistry = new ActionRegistry();
        try {
            this.launchpad = new ListenableMidiLaunchpad(DeviceDetector.detectDevices());
        } catch (MidiUnavailableException ignored) {
            throw new RuntimeException("Couldn't find input & output devices");
        }
        this.launchpad.setListener(this.actionRegistry);
        this.launchpadClient = (ListenableMidiLaunchpadClient) this.launchpad.getClient();

        this.server = new LpdeckServer();
        this.voicemeeter = new VoicemeeterIntegration();
    }

    public static @NotNull Lpdeck getInstance() {
        return INSTANCE;
    }

    public Path getRoamingPath() {
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            return Path.of(System.getenv("AppData")).resolve("lpdeck");
        } else if (os.startsWith("Linux")) {
            return Path.of(System.getenv("HOME")).resolve(".config/lpdeck");
        }
        return null;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File getRoamingFile(String name) throws IOException {
        File file = this.roamingPath.resolve(name).toFile();
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        return file;
    }

    public void start() {
        this.server.start();
        if (System.getProperty("os.name").startsWith("Windows")) {
            this.voicemeeter.start();
            SpotifyPatcher.patch(false);
            DiscordPatcher.patch(false);
        }

        this.actionRegistry.register();

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

        try {
            Voicemeeter.logout();
        } catch (NullPointerException ignored) {}
    }
}
