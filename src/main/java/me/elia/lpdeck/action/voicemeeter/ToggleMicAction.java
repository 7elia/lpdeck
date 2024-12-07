package me.elia.lpdeck.action.voicemeeter;

import me.elia.lpdeck.action.ToggleAction;
import me.mattco.voicemeeter.Voicemeeter;

public class ToggleMicAction extends ToggleAction {
    public ToggleMicAction(int x, int y) {
        super(x, y, Voicemeeter.getParameterFloat("Strip[0].B1") == 1.0F);
    }

    @Override
    public void toggle() {
        Voicemeeter.setParameterFloat("Strip[0].B1", this.value ? 0.0F : 1.0F);
        Voicemeeter.setParameterFloat("Strip[1].B1", this.value ? 1.0F : 0.0F);
    }
}
