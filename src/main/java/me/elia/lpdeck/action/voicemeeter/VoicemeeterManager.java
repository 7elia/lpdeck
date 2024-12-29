package me.elia.lpdeck.action.voicemeeter;

import me.elia.lpdeck.action.base.ActionCategory;
import me.elia.lpdeck.action.base.Manager;
import me.elia.lpdeck.voicemeeter.VoicemeeterListener;
import net.thecodersbreakfast.lp4j.api.Color;

public class VoicemeeterManager extends Manager implements VoicemeeterListener {
    public VoicemeeterManager() {
        super(ActionCategory.VOICEMEETER);
        this.client.getVoicemeeter().addListener(this);
    }

    @Override
    public void onPressed() {
        this.client.getVoicemeeter().restart();
    }

    @Override
    public void connected() {
        this.setColor(Color.GREEN);
    }

    @Override
    public void disconnected() {
        this.setColor(Color.RED);
    }
}
