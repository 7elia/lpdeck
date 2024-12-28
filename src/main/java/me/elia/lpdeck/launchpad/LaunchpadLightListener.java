package me.elia.lpdeck.launchpad;

import net.thecodersbreakfast.lp4j.api.Button;
import net.thecodersbreakfast.lp4j.api.Color;
import net.thecodersbreakfast.lp4j.api.LaunchpadListener;
import net.thecodersbreakfast.lp4j.api.Pad;

public interface LaunchpadLightListener extends LaunchpadListener {
    default void onPadLightChange(Pad pad, Color color) {}
    default void onButtonLightChange(Button button, Color color) {}
    @Override default void onPadPressed(Pad pad, long timestamp) {}
    @Override default void onPadReleased(Pad pad, long timestamp) {}
    @Override default void onButtonPressed(Button button, long timestamp) {}
    @Override default void onButtonReleased(Button button, long timestamp) {}
    @Override default void onTextScrolled(long timestamp) {}
}
