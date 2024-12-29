package me.elia.lpdeck.patch;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;

public class DiscordPatcher {
    private static final Logger LOGGER = LogManager.getLogger("Discord Patcher");
    private static final Path RENDERER_PATH = Path.of(System.getenv("AppData"), "Vencord", "dist", "renderer.js");
    private static final String REPLACEMENT_HEADER = "\n// ---- LP DECK REPLACE START ----\n";

    public static void patch() {
        try {
            String renderer = FileUtils.readFileToString(RENDERER_PATH.toFile(), StandardCharsets.UTF_8);
            if (renderer.contains(REPLACEMENT_HEADER)) {
                int replacementStart = renderer.indexOf(REPLACEMENT_HEADER);
                renderer = renderer.substring(0, replacementStart);
            }
            renderer += REPLACEMENT_HEADER + IOUtils.toString(Objects.requireNonNull(DiscordPatcher.class.getClassLoader().getResourceAsStream("patches/discord.js")), StandardCharsets.UTF_8);
            FileUtils.writeStringToFile(RENDERER_PATH.toFile(), renderer, StandardCharsets.UTF_8, false);
            LOGGER.info("Successfully patched Discord.");
        } catch (IOException e) {
            LOGGER.error("Error while patching renderer file", e);
        }
    }
}
