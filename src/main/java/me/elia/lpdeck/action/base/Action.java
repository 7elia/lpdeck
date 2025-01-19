package me.elia.lpdeck.action.base;

import lombok.Getter;
import me.elia.lpdeck.Lpdeck;
import net.thecodersbreakfast.lp4j.api.BackBufferOperation;
import net.thecodersbreakfast.lp4j.api.Color;
import net.thecodersbreakfast.lp4j.api.Pad;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

public abstract class Action {
    private static final Logger LOGGER = LogManager.getLogger("Action");
    @Getter private final String id;
    @Getter private final ActionCategory category;
    protected final Lpdeck client;
    @Getter private Point pos;

    public Action(String id, ActionCategory category) {
        this.id = id;
        this.category = category;
        this.client = Lpdeck.getInstance();
        this.setColor();
    }

    public abstract void press();

    public void setPos(Point pos) {
        this.setColor(Color.BLACK);
        this.pos = pos;
        this.setColor();
    }

    public void setColor() {
        this.setColor(Color.ORANGE);
    }

    public void setColor(Color color) {
        if (this.pos == null) {
            return;
        }
        try {
            this.client.getLaunchpadClient().setPadLight(Pad.at(this.pos.x, this.pos.y), color, BackBufferOperation.NONE);
        } catch (IllegalStateException e) {
            LOGGER.error("Error while setting pad color", e);
        }
    }

    public String getKey() {
        return this.category.name().toLowerCase() + "." + this.id;
    }

    @Override
    public String toString() {
        return this.getKey();
    }
}
