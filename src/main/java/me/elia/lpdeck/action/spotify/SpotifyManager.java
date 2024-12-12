package me.elia.lpdeck.action.spotify;

import me.elia.lpdeck.action.Manager;
import me.elia.lpdeck.spotify.SpotifyListener;
import net.thecodersbreakfast.lp4j.api.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SpotifyManager extends Manager implements SpotifyListener {
    private static final Logger LOGGER = LogManager.getLogger("Spotify Manager");

    public SpotifyManager(int pos) {
        super(pos);
        this.client.getSpotify().addListener(this);
    }

    @Override
    public void press() {
        try {
            this.client.getSpotify().restartServer();
        } catch (InterruptedException e) {
            LOGGER.error("Couldn't restart Spotify WS", e);
        }
    }

    @Override
    public void disconnected(int connections) {
        if (connections <= 0) {
            this.setColor(Color.RED);
        }
    }

    @Override
    public void connected() {
        this.setColor(Color.GREEN);
    }
}
