package me.elia.lpdeck.action.base;

import net.thecodersbreakfast.lp4j.api.Color;

public abstract class ToggleAction extends Action {
    public boolean value;

    public ToggleAction(String id, ActionCategory category) {
        this(id, category, false);
    }

    public ToggleAction(String id, ActionCategory category, boolean value) {
        super(id, category);
        this.value = value;
        this.setColor();
    }

    public abstract void toggle();

    @Override
    public void setColor() {
        this.setColor(this.value ? Color.GREEN : Color.RED);
    }

    @Override
    public final void onPress() {
        this.toggle();
        this.value = !this.value;
        this.setColor();
    }
}
