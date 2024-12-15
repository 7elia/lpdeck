package me.elia.lpdeck.voicemeeter;

import me.mattco.voicemeeter.Voicemeeter;
import me.mattco.voicemeeter.VoicemeeterException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class VoicemeeterIntegration {
    private static final Logger LOGGER = LogManager.getLogger("Voicemeeter Integration");
    private static final boolean IS_64_BIT = System.getProperty("os.arch").contains("64");
    private final List<VoicemeeterListener> listeners;
    private boolean connected;

    public VoicemeeterIntegration() {
        this.listeners = new ArrayList<>();
    }

    public void addListener(VoicemeeterListener listener) {
        if (this.connected) {
            listener.connected();
        } else {
            listener.disconnected();
        }
        this.listeners.add(listener);
    }

    public void start() {
        try {
            Voicemeeter.init(IS_64_BIT);
            Voicemeeter.login();
            this.connected = true;
            LOGGER.info("Connected to Voicemeeter.");
            for (VoicemeeterListener listener : this.listeners) {
                listener.connected();
            }
        } catch (VoicemeeterException ignored) {
            Voicemeeter.runVoicemeeter(2);
            LOGGER.info("Voicemeeter not opened yet, waiting...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored1) {
                LOGGER.warn("Couldn't successfully wait for Voicemeeter to start.");
            }
            Voicemeeter.logout();
            Voicemeeter.login();
            this.connected = true;
            LOGGER.info("Done waiting for Voicemeeter.");
        }
        this.testConnection();
    }

    public void restart() {
        try {
            Voicemeeter.logout();
            this.connected = false;
            for (VoicemeeterListener listener : this.listeners) {
                listener.disconnected();
            }
        } catch (VoicemeeterException e) {
            LOGGER.error("Error while disconnecting from Voicemeeter", e);
        }
        this.start();
    }

    public void testConnection() {
        try {
            Voicemeeter.getVoicemeeterVersion();
            this.connected = true;
            return;
        } catch (VoicemeeterException e) {
            LOGGER.error("Failed connection test", e);
        }
        this.connected = false;
        for (VoicemeeterListener listener : this.listeners) {
            listener.disconnected();
        }
    }

    public boolean usingMainMic() {
        Voicemeeter.areParametersDirty();
        return Voicemeeter.getParameterFloat("Strip[0].B1") == 1.0F;
    }

    public void swapMic() {
        boolean mainMic = this.usingMainMic();
        Voicemeeter.setParameterFloat("Strip[0].B1", mainMic ? 0.0F : 1.0F);
        Voicemeeter.setParameterFloat("Strip[1].B1", mainMic ? 1.0F : 0.0F);
        Voicemeeter.areParametersDirty();
        for (VoicemeeterListener listener : this.listeners) {
            listener.micChanged(!mainMic);
        }
    }
}
