package me.elia.lpdeck.action;

import lombok.Getter;
import me.elia.lpdeck.Lpdeck;
import net.thecodersbreakfast.lp4j.api.BackBufferOperation;
import net.thecodersbreakfast.lp4j.api.Color;
import net.thecodersbreakfast.lp4j.api.Pad;

public abstract class Action {
    public final Lpdeck client = Lpdeck.getInstance();
    @Getter private final int x;
    @Getter private final int y;

    public Action(int x, int y) {
        this.x = x;
        this.y = y;
        this.setColor();
    }

    public abstract void press();

    public void setColor() {
        this.setColor(Color.ORANGE);
    }

    public void setColor(Color color) {
        try {
            this.client.getLaunchpadClient().setPadLight(Pad.at(this.x, this.y), color, BackBufferOperation.NONE);
        } catch (IllegalStateException ignored) {}
    }
}
