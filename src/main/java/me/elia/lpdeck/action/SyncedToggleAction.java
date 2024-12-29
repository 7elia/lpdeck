package me.elia.lpdeck.action;

import com.google.gson.JsonObject;
import me.elia.lpdeck.action.base.ActionCategory;
import me.elia.lpdeck.action.base.ToggleAction;
import me.elia.lpdeck.server.ServerListener;
import me.elia.lpdeck.server.ServerTarget;

public class SyncedToggleAction extends ToggleAction implements ServerListener {
    private final ServerTarget target;
    private final String syncKey;

    public SyncedToggleAction(String id, ActionCategory category, ServerTarget target, String syncKey) {
        super(id, category);
        this.target = target;
        this.syncKey = syncKey;
        this.target.addListener(this);
    }

    @Override
    public void toggle() {
        this.target.sendCommand(this.getId());
    }

    @Override
    public void onDataSync(JsonObject data) {
        this.value = data.get(this.syncKey).getAsBoolean();
        this.setColor();
    }
}
