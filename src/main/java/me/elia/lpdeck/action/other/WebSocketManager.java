package me.elia.lpdeck.action.other;

import me.elia.lpdeck.action.Manager;
import net.thecodersbreakfast.lp4j.api.Color;

public class WebSocketManager extends Manager {
    public WebSocketManager(int pos) {
        super(pos, false);
        this.setColor(Color.RED);
    }

    @Override
    public void press() {
        this.client.getServer().restart();
    }
}
