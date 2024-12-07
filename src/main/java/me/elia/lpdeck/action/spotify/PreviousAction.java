package me.elia.lpdeck.action.spotify;

import me.elia.lpdeck.action.Action;

public class PreviousAction extends Action {
    public PreviousAction(int x, int y) {
        super(x, y);
    }

    @Override
    public void press() {
        this.client.getSpotify().broadcast("previous");
    }
}
