package me.elia.lpdeck.action.voicemeeter;

import me.elia.lpdeck.action.base.ActionCategory;
import me.elia.lpdeck.action.base.ToggleAction;
import me.elia.lpdeck.voicemeeter.VoicemeeterListener;
import me.mattco.voicemeeter.Voicemeeter;

public class ToggleMuteAction extends ToggleAction implements VoicemeeterListener {
    public ToggleMuteAction() {
        super("toggle_mute", ActionCategory.VOICEMEETER);
        this.client.getVoicemeeter().addListener(this);
    }

    @Override
    public void onConnected() {
        Voicemeeter.areParametersDirty();
        this.value = Voicemeeter.getParameterFloat("Bus[3].Mute") == 0.0F;
        this.setColor();
    }

    @Override
    public void toggle() {
        Voicemeeter.setParameterFloat("Bus[3].Mute", this.value ? 1.0F : 0.0F);
    }
}
