package me.elia.lpdeck.action;

import com.google.gson.JsonObject;
import me.elia.lpdeck.action.base.Action;
import me.elia.lpdeck.patch.SpotifyPatcher;
import me.elia.lpdeck.server.ServerListener;
import me.elia.lpdeck.server.ServerTarget;

public class SpotifyPatchAction extends Action implements ServerListener {
    private boolean playing;

    public SpotifyPatchAction(int x, int y) {
        super(x, y);
        ServerTarget.SPOTIFY.addListener(this);
    }

    @Override
    public void press() {
        if (this.playing) {
            ServerTarget.SPOTIFY.sendCommand("pause");
        }
        SpotifyPatcher.patch(true);
    }

    @Override
    public void onDataSync(JsonObject data) {
        this.playing = data.get("playing").getAsBoolean();
    }
}
