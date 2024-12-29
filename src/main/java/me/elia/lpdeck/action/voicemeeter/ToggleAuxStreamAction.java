package me.elia.lpdeck.action.voicemeeter;

import me.elia.lpdeck.action.base.ActionCategory;
import me.elia.lpdeck.action.base.ToggleAction;
import me.mattco.voicemeeter.Voicemeeter;

public class ToggleAuxStreamAction extends ToggleAction {
    public ToggleAuxStreamAction() {
        super(
                "toggle_aux_stream",
                ActionCategory.VOICEMEETER,
                Voicemeeter.getParameterFloat("Strip[4].B1") == 1.0F
        );
    }

    @Override
    public void toggle() {
        Voicemeeter.setParameterFloat("Strip[4].B1", this.value ? 0.0F : 1.0F);
    }
}
