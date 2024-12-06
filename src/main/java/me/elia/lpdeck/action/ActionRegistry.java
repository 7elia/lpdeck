package me.elia.lpdeck.action;

import lombok.RequiredArgsConstructor;
import net.thecodersbreakfast.lp4j.api.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ActionRegistry implements LaunchpadListener {
    private static final Logger LOGGER = LogManager.getLogger("Action Registry");
    private final LaunchpadClient client;
    private final List<Action> actions = new ArrayList<>();
    private final Map<Pad, Color> heldPads = new HashMap<>();

    public void addAction(Action action) {
        this.actions.add(action);
    }

    public void resetColors() {
        this.client.reset();
        for (Action action : this.actions) {
            if (action instanceof ButtonAction buttonAction) {
                this.client.setButtonLight(buttonAction.getButton(), buttonAction.getColor(), BackBufferOperation.NONE);
            } else if (action instanceof PadAction padAction) {
                this.client.setPadLight(padAction.getPad(), padAction.getColor(), BackBufferOperation.NONE);
            }
        }
    }

    @Override
    public void onPadPressed(Pad pad, long l) {
        for (Action action : this.actions) {
            if (action instanceof PadAction padAction) {
                if (padAction.getPad().equals(pad)) {
                    padAction.getCallback().call(pad);
                    this.heldPads.put(pad, padAction.getColor());
                    this.client.setPadLight(pad, Color.BLACK, BackBufferOperation.NONE);
                    LOGGER.info("Activated pad action at ({}, {})", pad.getX(), pad.getY());
                    return;
                }
            }
        }
        LOGGER.info("No action registered for pad ({}, {})", pad.getX(), pad.getY());
    }

    @Override
    public void onPadReleased(Pad pad, long l) {
        if (this.heldPads.containsKey(pad)) {
            this.client.setPadLight(pad, this.heldPads.get(pad), BackBufferOperation.NONE);
            this.heldPads.remove(pad);
        }
    }

    @Override
    public void onButtonPressed(Button button, long l) {
        for (Action action : this.actions) {
            if (action instanceof ButtonAction buttonAction) {
                if (buttonAction.getButton().equals(button)) {
                    buttonAction.getCallback().call(button);
                    LOGGER.info("Activated button action at {} ({})", button.getCoordinate(), button.isTopButton() ? "Top" : "Right");
                    return;
                }
            }
        }
        LOGGER.info("No action registered for button {} ({})", button.getCoordinate(), button.isTopButton() ? "Top" : "Right");
    }

    @Override
    public void onButtonReleased(Button button, long l) {}

    @Override
    public void onTextScrolled(long l) {}
}
