package me.elia.lpdeck.action;

import me.elia.lpdeck.action.base.ActionCategory;
import me.elia.lpdeck.action.base.Manager;
import net.thecodersbreakfast.lp4j.api.Color;

public class WebSocketManager extends Manager {
    public WebSocketManager() {
        super(ActionCategory.MISC);
        this.setColor(Color.RED);
    }

    @Override
    public void setColor() {
        this.setColor(Color.RED);
    }

    @Override
    public void onPress() {
        this.client.getServer().restart();
    }
}
