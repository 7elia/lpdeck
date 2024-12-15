package me.elia.lpdeck.action;

import me.elia.lpdeck.action.base.Manager;
import net.thecodersbreakfast.lp4j.api.Color;

public class WebSocketManager extends Manager {
    public WebSocketManager(int pos) {
        super(pos, false);
        this.setColor(Color.RED);
    }

    @Override
    public void onPressed() {
        this.client.getServer().restart();
    }
}
