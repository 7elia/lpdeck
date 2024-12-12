package me.elia.lpdeck.action.voicemeeter;

import me.elia.lpdeck.action.ToggleAction;
import me.mattco.voicemeeter.Voicemeeter;

public class ToggleSpeakersAction extends ToggleAction {
    public ToggleSpeakersAction(int x, int y) {
        super(x, y, isUsingSpeakers());
    }

    private static boolean isUsingSpeakers() {
        Voicemeeter.areParametersDirty();
        return Voicemeeter.getParameterFloat("Strip[3].A2") == 1.0F ||
               Voicemeeter.getParameterFloat("Strip[4].A2") == 1.0F;
    }

    @Override
    public void toggle() {
        Voicemeeter.setParameterFloat("Strip[3].A2", this.value ? 0.0F : 1.0F);
        Voicemeeter.setParameterFloat("Strip[4].A2", this.value ? 0.0F : 1.0F);
    }
}
