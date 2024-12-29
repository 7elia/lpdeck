package me.elia.lpdeck.action.voicemeeter;

import me.elia.lpdeck.action.base.ActionCategory;
import me.elia.lpdeck.action.base.ToggleAction;
import me.mattco.voicemeeter.Voicemeeter;

public class ToggleMuteAction extends ToggleAction {
    public ToggleMuteAction() {
        super(
                "toggle_mute",
                ActionCategory.VOICEMEETER,
                Voicemeeter.getParameterFloat("Bus[3].Mute") == 0.0F
        );
    }

    @Override
    public void toggle() {
        Voicemeeter.setParameterFloat("Bus[3].Mute", this.value ? 1.0F : 0.0F);
    }
}
