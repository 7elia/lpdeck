package me.elia.lpdeck.action.discord;

import me.elia.lpdeck.DiscordPatcher;
import me.elia.lpdeck.action.Action;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class PatchAction extends Action {
    public PatchAction(int x, int y) {
        super(x, y);
    }

    @Override
    public void press() {
        DiscordPatcher.patch();
    }
}
