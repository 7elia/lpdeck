package me.elia.lpdeck.action.base;

import lombok.RequiredArgsConstructor;
import net.thecodersbreakfast.lp4j.api.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ActionRegistry implements LaunchpadListener {
    private static final Logger LOGGER = LogManager.getLogger("Action Registry");
    private final List<Action> actions = new ArrayList<>();
    private final List<Manager> managers = new ArrayList<>();

    public void addAction(Action action) {
        this.actions.add(action);
    }

    public void addManager(Manager manager) {
        this.managers.add(manager);
    }

    @Override
    public void onPadPressed(Pad pad, long l) {
        for (Action action : this.actions) {
            if (action.getX() == pad.getX() && action.getY() == pad.getY()) {
                action.press();
                LOGGER.info("Activated pad action at ({}, {}).", pad.getX(), pad.getY());
                return;
            }
        }
        LOGGER.info("No action registered for pad ({}, {}).", pad.getX(), pad.getY());
    }

    @Override
    public void onPadReleased(Pad pad, long l) {}

    @Override
    public void onButtonPressed(Button button, long l) {
        for (Manager manager : this.managers) {
            if (button.isTopButton() && button.getCoordinate() == manager.getPos()) {
                manager.onPressed();
            }
        }
    }

    @Override
    public void onButtonReleased(Button button, long l) {}

    @Override
    public void onTextScrolled(long l) {}
}
