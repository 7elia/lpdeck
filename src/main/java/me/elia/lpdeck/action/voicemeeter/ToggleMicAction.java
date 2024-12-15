package me.elia.lpdeck.action.voicemeeter;

import me.elia.lpdeck.action.base.ToggleAction;
import me.mattco.voicemeeter.Voicemeeter;

public class ToggleMicAction extends ToggleAction {
    public ToggleMicAction(int x, int y) {
        super(x, y, Voicemeeter.getParameterFloat("Strip[0].B1") == 1.0F);
    }

    @Override
    public void toggle() {
        this.client.getVoicemeeter().swapMic();
    }
}
