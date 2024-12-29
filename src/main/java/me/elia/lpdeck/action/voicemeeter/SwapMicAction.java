package me.elia.lpdeck.action.voicemeeter;

import me.elia.lpdeck.action.base.ActionCategory;
import me.elia.lpdeck.action.base.ToggleAction;
import me.elia.lpdeck.voicemeeter.VoicemeeterListener;
import me.mattco.voicemeeter.Voicemeeter;

public class SwapMicAction extends ToggleAction implements VoicemeeterListener {
    public SwapMicAction() {
        super("swap_mic", ActionCategory.VOICEMEETER);
        this.client.getVoicemeeter().addListener(this);
    }

    @Override
    public void onConnected() {
        Voicemeeter.areParametersDirty();
        this.value = Voicemeeter.getParameterFloat("Strip[0].B1") == 1.0F;
        this.setColor();
    }

    @Override
    public void toggle() {
        this.client.getVoicemeeter().swapMic();
    }
}
