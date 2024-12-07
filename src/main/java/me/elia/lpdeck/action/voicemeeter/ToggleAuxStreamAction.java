package me.elia.lpdeck.action.voicemeeter;

import me.elia.lpdeck.action.ToggleAction;
import me.mattco.voicemeeter.Voicemeeter;

public class ToggleAuxStreamAction extends ToggleAction {
    public ToggleAuxStreamAction(int x, int y) {
        super(x, y, Voicemeeter.getParameterFloat("Strip[4].B1") == 1.0F);
    }

    @Override
    public void toggle() {
        Voicemeeter.setParameterFloat("Strip[4].B1", this.value ? 0.0F : 1.0F);
    }
}
