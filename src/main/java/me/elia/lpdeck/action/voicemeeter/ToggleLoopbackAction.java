package me.elia.lpdeck.action.voicemeeter;

import me.elia.lpdeck.Lpdeck;
import me.elia.lpdeck.action.base.ActionCategory;
import me.elia.lpdeck.action.base.ToggleAction;
import me.elia.lpdeck.voicemeeter.VoicemeeterListener;
import me.mattco.voicemeeter.Voicemeeter;

public class ToggleLoopbackAction extends ToggleAction implements VoicemeeterListener {
    public ToggleLoopbackAction() {
        super(
                "toggle_loopback",
                ActionCategory.VOICEMEETER,
                Voicemeeter.getParameterFloat(getStrip()) == 1.0F
        );
        this.client.getVoicemeeter().addListener(this);
    }

    private static String getStrip() {
        Voicemeeter.areParametersDirty();
        return getStrip(Lpdeck.getInstance().getVoicemeeter().usingMainMic());
    }

    private static String getStrip(boolean main) {
        return "Strip[" + (main ? "0" : "1") + "].A1";
    }

    @Override
    public void toggle() {
        Voicemeeter.setParameterFloat(getStrip(), this.value ? 0.0F : 1.0F);
    }

    @Override
    public void micChanged(boolean main) {
        if (!this.value) {
            return;
        }
        Voicemeeter.setParameterFloat(getStrip(!main), 0.0F);
        Voicemeeter.setParameterFloat(getStrip(main), 1.0F);
    }
}
