package me.elia.lpdeck.action;

import me.elia.lpdeck.action.base.ActionCategory;
import me.elia.lpdeck.patch.DiscordPatcher;
import me.elia.lpdeck.action.base.Action;
import me.elia.lpdeck.server.ServerTarget;

public class DiscordPatchAction extends Action {
    public DiscordPatchAction() {
        super("patch_discord", ActionCategory.DISCORD);
    }

    @Override
    public void press() {
        DiscordPatcher.patch();
        ServerTarget.DISCORD.sendCommand("restart");
    }
}
