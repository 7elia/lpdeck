package me.elia.lpdeck.action;

import lombok.Getter;
import me.elia.lpdeck.Lpdeck;
import net.thecodersbreakfast.lp4j.api.BackBufferOperation;
import net.thecodersbreakfast.lp4j.api.Button;
import net.thecodersbreakfast.lp4j.api.Color;

public abstract class Manager {
    @Getter private final int pos;
    private final Button button;
    public final Lpdeck client;

    public Manager(int pos) {
        this.pos = pos;
        this.button = Button.atTop(pos);
        this.client = Lpdeck.getInstance();
    }

    public abstract void press();

    public void setColor(Color color) {
        this.client.getLaunchpadClient().setButtonLight(this.button, color, BackBufferOperation.NONE);
    }
}
