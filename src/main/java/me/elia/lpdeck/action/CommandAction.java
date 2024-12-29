package me.elia.lpdeck.action;

import me.elia.lpdeck.action.base.Action;
import me.elia.lpdeck.action.base.ActionCategory;
import me.elia.lpdeck.server.ServerTarget;

public class CommandAction extends Action {
    private final ServerTarget target;

    public CommandAction(String id, ActionCategory category, ServerTarget target) {
        super(id, category);
        this.target = target;
    }

    @Override
    public void press() {
        this.target.sendCommand(this.getId());
    }
}
