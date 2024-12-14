package me.elia.lpdeck.action.discord;

import me.elia.lpdeck.DiscordPatcher;
import me.elia.lpdeck.action.Action;
import me.elia.lpdeck.server.ServerTarget;

public class PatchAction extends Action {
    public PatchAction(int x, int y) {
        super(x, y);
    }

    @Override
    public void press() {
        DiscordPatcher.patch();
        ServerTarget.DISCORD.sendCommand("restart");
    }
}
