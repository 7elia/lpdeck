package me.elia.lpdeck.launchpad;

import net.thecodersbreakfast.lp4j.api.BackBufferOperation;
import net.thecodersbreakfast.lp4j.api.Button;
import net.thecodersbreakfast.lp4j.api.Color;
import net.thecodersbreakfast.lp4j.api.Pad;
import net.thecodersbreakfast.lp4j.midi.MidiLaunchpadClient;
import net.thecodersbreakfast.lp4j.midi.protocol.MidiProtocolClient;

import java.util.ArrayList;
import java.util.List;

public class ListenableMidiLaunchpadClient extends MidiLaunchpadClient {
    private final List<LaunchpadLightListener> listeners;

    public ListenableMidiLaunchpadClient(MidiProtocolClient midiProtocolClient) {
        super(midiProtocolClient);
        this.listeners = new ArrayList<>();
    }

    public void addLightListener(LaunchpadLightListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void setPadLight(Pad pad, Color color, BackBufferOperation operation) {
        super.setPadLight(pad, color, operation);
        for (LaunchpadLightListener listener : this.listeners) {
            listener.onPadLightChange(pad, color);
        }
    }

    @Override
    public void setButtonLight(Button button, Color color, BackBufferOperation operation) {
        super.setButtonLight(button, color, operation);
        for (LaunchpadLightListener listener : this.listeners) {
            listener.onButtonLightChange(button, color);
        }
    }
}
