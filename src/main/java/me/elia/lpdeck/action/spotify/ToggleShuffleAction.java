package me.elia.lpdeck.action.spotify;

import com.google.gson.JsonObject;
import me.elia.lpdeck.action.ToggleAction;
import me.elia.lpdeck.server.ServerListener;
import me.elia.lpdeck.server.ServerTarget;

public class ToggleShuffleAction extends ToggleAction implements ServerListener {
    public ToggleShuffleAction(int x, int y) {
        super(x, y);
        ServerTarget.SPOTIFY.addListener(this);
    }

    @Override
    public void toggle() {
        ServerTarget.SPOTIFY.sendCommand("toggle_shuffle");
    }

    @Override
    public void onDataSync(JsonObject data) {
        this.value = data.get("shuffle").getAsBoolean();
        this.setColor();
    }
}
