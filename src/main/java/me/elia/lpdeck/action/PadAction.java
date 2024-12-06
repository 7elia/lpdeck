package me.elia.lpdeck.action;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.thecodersbreakfast.lp4j.api.Color;
import net.thecodersbreakfast.lp4j.api.Pad;

@Getter
public class PadAction extends Action {
    private final Pad pad;
    private final PadCallback callback;

    public PadAction(Pad pad, Color color, PadCallback callback) {
        super(color);
        this.pad = pad;
        this.callback = callback;
    }

    public interface PadCallback {
        void call(Pad pad);
    }
}
