package me.elia.lpdeck.action;

import me.elia.lpdeck.action.base.Action;
import me.elia.lpdeck.server.ServerTarget;

public class CommandAction extends Action {
    private final ServerTarget target;
    private final String command;

    public CommandAction(int x, int y, ServerTarget target, String command) {
        super(x, y);
        this.target = target;
        this.command = command;
    }

    @Override
    public void press() {
        this.target.sendCommand(this.command);
    }
}
