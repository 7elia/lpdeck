package me.elia.lpdeck.action;

import me.elia.lpdeck.action.base.ActionCategory;
import me.elia.lpdeck.action.base.Manager;
import me.elia.lpdeck.server.ServerListener;
import me.elia.lpdeck.server.ServerTarget;
import net.thecodersbreakfast.lp4j.api.Color;

public class ServerTargetManager extends Manager implements ServerListener {
    private final ServerTarget target;

    public ServerTargetManager(ActionCategory category, ServerTarget target) {
        super(category);
        this.target = target;
        this.target.addListener(this);
        this.setColor();
    }

    @Override
    public void setColor() {
        this.setColor(this.client.getServer().hasClientsFor(this.target) ? Color.GREEN : Color.RED);
    }

    @Override
    public void onPressed() {
        this.client.getServer().closeTarget(this.target);
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
