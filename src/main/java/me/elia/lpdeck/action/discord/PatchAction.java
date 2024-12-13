package me.elia.lpdeck.action.discord;

import me.elia.lpdeck.DiscordPatcher;
import me.elia.lpdeck.action.Action;

public class PatchAction extends Action {
    public PatchAction(int x, int y) {
        super(x, y);
    }

    @Override
    public void press() {
        DiscordPatcher.patch();
    }
}
