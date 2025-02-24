package me.elia.lpdeck.action.base;

import lombok.Getter;
import me.elia.lpdeck.Lpdeck;
import net.thecodersbreakfast.lp4j.api.BackBufferOperation;
import net.thecodersbreakfast.lp4j.api.Button;
import net.thecodersbreakfast.lp4j.api.Color;

public abstract class Manager {
    @Getter private final ActionCategory category;
    @Getter private Button pos;
    protected final Lpdeck client;

    public Manager(ActionCategory category) {
        this.category = category;
        this.client = Lpdeck.getInstance();
    }

    public abstract void onPress();

    public void setPos(Button pos) {
        this.setColor(Color.BLACK);
        this.pos = pos;
        this.setColor();
    }

    public void setColor() {
        this.setColor(Color.GREEN);
    }

    public void setColor(Color color) {
        if (this.pos == null) {
            return;
        }
        this.client.getLaunchpadClient().setButtonLight(this.pos, color, BackBufferOperation.NONE);
    }

    @Override
    public String toString() {
        return this.category.name();
    }
}
