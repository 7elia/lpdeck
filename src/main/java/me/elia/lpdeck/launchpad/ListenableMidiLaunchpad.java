package me.elia.lpdeck.launchpad;

import net.thecodersbreakfast.lp4j.api.LaunchpadClient;
import net.thecodersbreakfast.lp4j.api.LaunchpadException;
import net.thecodersbreakfast.lp4j.midi.MidiDeviceConfiguration;
import net.thecodersbreakfast.lp4j.midi.MidiLaunchpad;
import net.thecodersbreakfast.lp4j.midi.protocol.DefaultMidiProtocolClient;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

public class ListenableMidiLaunchpad extends MidiLaunchpad {
    private final Receiver receiver;

    public ListenableMidiLaunchpad(MidiDeviceConfiguration configuration) throws MidiUnavailableException {
        super(configuration);
        this.receiver = configuration.getOutputDevice().getReceiver();
    }

    @Override
    public LaunchpadClient getClient() {
        if (this.receiver == null) {
            throw new LaunchpadException("Unable to provide a client, because no Receiver or Output Device have been configured.");
        }
        return new ListenableMidiLaunchpadClient(new DefaultMidiProtocolClient(this.receiver));
    }
}
