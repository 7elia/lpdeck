package me.elia.lpdeck.action.spotify;

import me.elia.lpdeck.Lpdeck;
import me.elia.lpdeck.action.ToggleAction;
import me.elia.lpdeck.spotify.SpotifyListener;
import me.elia.lpdeck.spotify.SpotifyServerCommand;
import me.elia.lpdeck.spotify.SpotifyState;

public class ToggleRepeatAction extends ToggleAction implements SpotifyListener {
    public ToggleRepeatAction(int x, int y) {
        super(x, y, Lpdeck.getInstance().getSpotify().getState().isRepeat());
        this.client.getSpotify().addListener(this);
    }

    @Override
    public void toggle() {
        this.client.getSpotify().sendCommand(SpotifyServerCommand.TOGGLE_REPEAT);
    }

    @Override
    public void updated(SpotifyState state) {
        this.value = state.isRepeat();
        this.setColor();
    }
}
