package me.elia.lpdeck.action.voicemeeter;

import me.elia.lpdeck.action.Manager;
import me.mattco.voicemeeter.Voicemeeter;
import me.mattco.voicemeeter.VoicemeeterException;
import net.thecodersbreakfast.lp4j.api.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VoicemeeterManager extends Manager {
    private static final Logger LOGGER = LogManager.getLogger("Voicemeeter Manager");

    public VoicemeeterManager(int pos) {
        super(pos);
        this.testConnection();
    }

    private void testConnection() {
        boolean connected;
        try {
            Voicemeeter.getVoicemeeterVersion();
            connected = true;
        } catch (VoicemeeterException ignored) {
            connected = false;
        }
        this.setColor(connected ? Color.GREEN : Color.RED);
    }

    @Override
    public void press() {
        this.setColor(Color.RED);
        try {
            Voicemeeter.logout();
            Voicemeeter.init(System.getProperty("os.arch").contains("64"));
            Voicemeeter.login();
            LOGGER.info("Restarted Voicemeeter instance");
        } catch (VoicemeeterException e) {
            LOGGER.error("Couldn't restart Voicemeeter instance", e);
        }
        this.testConnection();
    }
}
