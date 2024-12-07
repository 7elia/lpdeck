package me.elia.lpdeck.action.voicemeeter;

import me.elia.lpdeck.action.ToggleAction;
import me.mattco.voicemeeter.Voicemeeter;

public class ToggleMuteAction extends ToggleAction {
    public ToggleMuteAction(int x, int y) {
        super(x, y, Voicemeeter.getParameterFloat("Bus[3].Mute") == 0.0F);
    }

    @Override
    public void toggle() {
        Voicemeeter.setParameterFloat("Bus[3].Mute", this.value ? 1.0F : 0.0F);
    }
}
