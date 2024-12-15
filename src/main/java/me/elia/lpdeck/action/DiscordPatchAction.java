package me.elia.lpdeck.action;

import me.elia.lpdeck.patch.DiscordPatcher;
import me.elia.lpdeck.action.base.Action;
import me.elia.lpdeck.server.ServerTarget;

public class DiscordPatchAction extends Action {
    public DiscordPatchAction(int x, int y) {
        super(x, y);
    }

    @Override
    public void press() {
        DiscordPatcher.patch();
        ServerTarget.DISCORD.sendCommand("restart");
    }
}
