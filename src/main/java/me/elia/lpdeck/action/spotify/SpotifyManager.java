package me.elia.lpdeck.action.spotify;

import me.elia.lpdeck.action.Manager;
import me.elia.lpdeck.server.ServerListener;
import me.elia.lpdeck.server.ServerTarget;
import net.thecodersbreakfast.lp4j.api.Color;

public class SpotifyManager extends Manager implements ServerListener {
    public SpotifyManager(int pos) {
        super(pos);
        ServerTarget.SPOTIFY.addListener(this);
        this.setColor(this.client.getServer().hasClientsFor(ServerTarget.SPOTIFY) ? Color.GREEN : Color.RED);
    }

    @Override
    public void press() {
        this.client.getServer().closeTarget(ServerTarget.SPOTIFY);
    }

    @Override
    public void onClientConnected() {
        this.setColor(Color.GREEN);
    }

    @Override
    public void onClientDisconnected(int connections) {
        if (connections <= 0) {
            this.setColor(Color.RED);
        }
    }
}
