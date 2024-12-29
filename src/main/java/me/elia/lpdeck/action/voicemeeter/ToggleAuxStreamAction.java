package me.elia.lpdeck.action.voicemeeter;

import me.elia.lpdeck.action.base.ActionCategory;
import me.elia.lpdeck.action.base.ToggleAction;
import me.elia.lpdeck.voicemeeter.VoicemeeterListener;
import me.mattco.voicemeeter.Voicemeeter;

public class ToggleAuxStreamAction extends ToggleAction implements VoicemeeterListener {
    public ToggleAuxStreamAction() {
        super("toggle_aux_stream", ActionCategory.VOICEMEETER);
        this.client.getVoicemeeter().addListener(this);
    }

    @Override
    public void onConnected() {
        Voicemeeter.areParametersDirty();
        this.value = Voicemeeter.getParameterFloat("Strip[4].B1") == 1.0F;
        this.setColor();
    }

    @Override
    public void toggle() {
        Voicemeeter.setParameterFloat("Strip[4].B1", this.value ? 0.0F : 1.0F);
    }
}
