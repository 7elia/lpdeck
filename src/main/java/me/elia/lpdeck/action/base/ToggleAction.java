package me.elia.lpdeck.action.base;

import net.thecodersbreakfast.lp4j.api.Color;

public abstract class ToggleAction extends Action {
    public boolean value;

    public ToggleAction(int x, int y) {
        this(x, y, false);
    }

    public ToggleAction(int x, int y, boolean value) {
        super(x, y);
        this.value = value;
        this.setColor();
    }

    public abstract void toggle();

    @Override
    public void setColor() {
        this.setColor(this.value ? Color.GREEN : Color.RED);
    }

    @Override
    public final void press() {
        this.toggle();
        this.value = !this.value;
        this.setColor();
    }
}
