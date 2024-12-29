package me.elia.lpdeck.action.voicemeeter;

import me.elia.lpdeck.action.base.ActionCategory;
import me.elia.lpdeck.action.base.ToggleAction;
import me.mattco.voicemeeter.Voicemeeter;

public class SwapMicAction extends ToggleAction {
    public SwapMicAction() {
        super(
                "swap_mic",
                ActionCategory.VOICEMEETER,
                Voicemeeter.getParameterFloat("Strip[0].B1") == 1.0F
        );
    }

    @Override
    public void toggle() {
        this.client.getVoicemeeter().swapMic();
    }
}
