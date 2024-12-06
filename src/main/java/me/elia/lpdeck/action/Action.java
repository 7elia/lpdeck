package me.elia.lpdeck.action;

import lombok.Getter;
import net.thecodersbreakfast.lp4j.api.Color;

@Getter
public abstract class Action {
    private final Color color;

    public Action(Color color) {
        this.color = color;
    }
}
