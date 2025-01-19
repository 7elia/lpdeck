package me.elia.lpdeck.patch;

import me.elia.lpdeck.Main;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class SpotifyPatcher {
    private static final Logger LOGGER = LogManager.getLogger("Spotify Patcher");
    private static final Path NPM_BIN_PATH = Path.of(System.getenv("AppData"), "npm", "npm.cmd");
    private static final Path SPICETIFY_BIN_PATH = Path.of(System.getenv("LocalAppData"), "spicetify", "spicetify.exe");
    private static final Path EXTENSIONS_PATH = Path.of(System.getenv("AppData"), "spicetify", "Extensions");
    private static final Path PACKAGE_ROOT_PATH = Path.of(System.getProperty("user.dir"), "lpdeck-spotify");
    private static final Path PATCHES_PATH = Path.of(System.getProperty("user.dir"), "src", "main", "resources", "patches");
    private static final Path DIST_PATH = PACKAGE_ROOT_PATH.resolve("dist");

    public static void patch(boolean force) {
        if (!EXTENSIONS_PATH.toFile().exists()) {
            LOGGER.error("Extensions folder not found, Spicetify is likely not installed.");
            return;
        }

        if (!force && EXTENSIONS_PATH.resolve("lpdeck.js").toFile().exists()) {
            LOGGER.info("Extension file already exists, skipped Spotify patch.");
            return;
        }

        if (Main.IS_DEVELOPMENT) {
            LOGGER.info("Starting Spicetify extension build...");
            if (!runCommand(NPM_BIN_PATH.toString(), "run", "build-local")) {
                LOGGER.error("Spicetify extension build failed.");
                return;
            }

            LOGGER.info("Copying built extension file to extensions folder...");
            try {
                Files.copy(DIST_PATH.resolve("lpdeck.js"), PATCHES_PATH.resolve("spotify.js"), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(DIST_PATH.resolve("lpdeck.js"), EXTENSIONS_PATH.resolve("lpdeck.js"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                LOGGER.error("Error while copying built extension file", e);
                return;
            }
        } else {
            LOGGER.info("Copying extension file to extensions folder...");
            try {
                String content = IOUtils.toString(Objects.requireNonNull(DiscordPatcher.class.getClassLoader().getResourceAsStream("patches/spotify.js")), StandardCharsets.UTF_8);
                FileUtils.writeStringToFile(EXTENSIONS_PATH.resolve("lpdeck.js").toFile(), content, StandardCharsets.UTF_8);
            } catch (IOException e) {
                LOGGER.error("Error while copying built extension file", e);
                return;
            }
        }

        LOGGER.info("Applying Spicetify changes...");
        if (!runCommand(SPICETIFY_BIN_PATH.toString(), "apply")) {
            LOGGER.error("Spicetify apply failed.");
            return;
        }

        LOGGER.info("Successfully built & applied Spicetify extension.");
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean runCommand(String... command) {
        try {
            Process process = new ProcessBuilder()
                    .command(command)
                    .directory(PACKAGE_ROOT_PATH.toFile())
                    .start();

            try (
                    BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))
            ) {
                String line;
                while ((line = outputReader.readLine()) != null) {
                    LOGGER.debug(line);
                }
                while ((line = errorReader.readLine()) != null) {
                    LOGGER.error(line);
                }
            }

            return process.waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error while running command '{}'", command, e);
            return false;
        }
    }
}
