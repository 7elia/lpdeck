package me.elia.lpdeck.action.spotify;

import me.elia.lpdeck.action.Action;
import me.elia.lpdeck.spotify.SpotifyServerCommand;

public class SpotifyCommandAction extends Action {
    private final SpotifyServerCommand command;

    public SpotifyCommandAction(int x, int y, SpotifyServerCommand command) {
        super(x, y);
        this.command = command;
    }

    @Override
    public void press() {
        this.client.getSpotify().sendCommand(this.command);
    }
}
