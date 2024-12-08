package me.elia.lpdeck.action.spotify;

import me.elia.lpdeck.Lpdeck;
import me.elia.lpdeck.action.ToggleAction;
import me.elia.lpdeck.spotify.SpotifyListener;
import me.elia.lpdeck.spotify.SpotifyServerCommand;
import me.elia.lpdeck.spotify.SpotifyState;

public class TogglePlayAction extends ToggleAction implements SpotifyListener {
    public TogglePlayAction(int x, int y) {
        super(x, y, Lpdeck.getInstance().getSpotify().getState().isPlaying());
        this.client.getSpotify().addListener(this);
    }

    @Override
    public void toggle() {
        this.client.getSpotify().sendCommand(SpotifyServerCommand.TOGGLE_PLAY);
    }

    @Override
    public void updated(SpotifyState state) {
        this.value = state.isPlaying();
        this.setColor();
    }
}
