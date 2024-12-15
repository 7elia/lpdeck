package me.elia.lpdeck.action;

import com.google.gson.JsonObject;
import me.elia.lpdeck.action.base.ToggleAction;
import me.elia.lpdeck.server.ServerListener;
import me.elia.lpdeck.server.ServerTarget;

public class SyncedToggleAction extends ToggleAction implements ServerListener {
    private final ServerTarget target;
    private final String command;
    private final String syncKey;

    public SyncedToggleAction(int x, int y, ServerTarget target, String command, String syncKey) {
        super(x, y);
        this.target = target;
        this.command = command;
        this.syncKey = syncKey;
        this.target.addListener(this);
    }

    @Override
    public void toggle() {
        this.target.sendCommand(this.command);
    }

    @Override
    public void onDataSync(JsonObject data) {
        this.value = data.get(this.syncKey).getAsBoolean();
        this.setColor();
    }
}
