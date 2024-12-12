package me.elia.lpdeck.action.voicemeeter;

import me.elia.lpdeck.action.Manager;
import me.elia.lpdeck.voicemeeter.VoicemeeterListener;
import net.thecodersbreakfast.lp4j.api.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VoicemeeterManager extends Manager implements VoicemeeterListener {
    private static final Logger LOGGER = LogManager.getLogger("Voicemeeter Manager");

    public VoicemeeterManager(int pos) {
        super(pos);
        this.client.getVoicemeeter().addListener(this);
    }

    @Override
    public void press() {
        this.client.getVoicemeeter().restart();
    }

    @Override
    public void connected() {
        LOGGER.info("Connected");
        this.setColor(Color.GREEN);
    }

    @Override
    public void disconnected() {
        LOGGER.info("Disconnected");
        this.setColor(Color.RED);
    }
}
