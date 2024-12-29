package me.elia.lpdeck.action;

import me.elia.lpdeck.action.base.ActionCategory;
import me.elia.lpdeck.action.base.Manager;
import me.elia.lpdeck.server.ServerListener;
import me.elia.lpdeck.server.ServerTarget;
import net.thecodersbreakfast.lp4j.api.Color;

public class ServerTargetManager extends Manager implements ServerListener {
    private final ServerTarget target;
    private final ServerTargetManagerCallback callback;

    public ServerTargetManager(ActionCategory category, ServerTarget target, ServerTargetManagerCallback callback) {
        super(category);
        this.target = target;
        this.callback = callback;
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
        this.callback.callback();
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

    public interface ServerTargetManagerCallback {
        void callback();
    }
}
