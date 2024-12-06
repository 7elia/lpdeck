package me.elia.lpdeck.action;

import lombok.Getter;
import net.thecodersbreakfast.lp4j.api.Button;
import net.thecodersbreakfast.lp4j.api.Color;

@Getter
public class ButtonAction extends Action {
    private final Button button;
    private final ButtonCallback callback;

    public ButtonAction(Button button, Color color, ButtonCallback callback) {
        super(color);
        this.button = button;
        this.callback = callback;
    }

    public interface ButtonCallback {
        void call(Button button);
    }
}
