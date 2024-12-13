package me.elia.lpdeck.action;

import me.elia.lpdeck.server.ServerListener;
import me.elia.lpdeck.server.ServerTarget;
import net.thecodersbreakfast.lp4j.api.Color;

public class ServerManager extends Manager implements ServerListener {
    private final ServerTarget target;

    public ServerManager(int pos, ServerTarget target) {
        super(pos);
        this.target = target;
        this.target.addListener(this);
        this.setColor(this.client.getServer().hasClientsFor(this.target) ? Color.GREEN : Color.RED);
    }

    @Override
    public void press() {
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
