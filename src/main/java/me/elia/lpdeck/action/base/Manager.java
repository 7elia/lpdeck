package me.elia.lpdeck.action.base;

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
        this(pos, true);
    }

    public Manager(int pos, boolean top) {
        this.pos = pos;
        this.button = top ? Button.atTop(pos) : Button.atRight(pos);
        this.client = Lpdeck.getInstance();
    }

    public abstract void onPressed();

    public void setColor(Color color) {
        this.client.getLaunchpadClient().setButtonLight(this.button, color, BackBufferOperation.NONE);
    }
}
