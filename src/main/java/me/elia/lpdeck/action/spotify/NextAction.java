package me.elia.lpdeck.action.spotify;

import me.elia.lpdeck.action.Action;

public class NextAction extends Action {
    public NextAction(int x, int y) {
        super(x, y);
    }

    @Override
    public void press() {
        this.client.getSpotify().broadcast("next");
    }
}
