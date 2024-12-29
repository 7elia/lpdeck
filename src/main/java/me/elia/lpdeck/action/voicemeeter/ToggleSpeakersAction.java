package me.elia.lpdeck.action.voicemeeter;

import me.elia.lpdeck.action.base.ActionCategory;
import me.elia.lpdeck.action.base.ToggleAction;
import me.elia.lpdeck.voicemeeter.VoicemeeterListener;
import me.mattco.voicemeeter.Voicemeeter;

public class ToggleSpeakersAction extends ToggleAction implements VoicemeeterListener {
    public ToggleSpeakersAction() {
        super("toggle_speakers", ActionCategory.VOICEMEETER);
        this.client.getVoicemeeter().addListener(this);
    }

    @Override
    public void onConnected() {
        Voicemeeter.areParametersDirty();
        this.value = Voicemeeter.getParameterFloat("Strip[3].A2") == 1.0F ||
                     Voicemeeter.getParameterFloat("Strip[4].A2") == 1.0F;
        this.setColor();
    }

    @Override
    public void toggle() {
        Voicemeeter.setParameterFloat("Strip[3].A2", this.value ? 0.0F : 1.0F);
        Voicemeeter.setParameterFloat("Strip[4].A2", this.value ? 0.0F : 1.0F);
    }
}
