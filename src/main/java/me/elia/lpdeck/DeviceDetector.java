package me.elia.lpdeck;

import net.thecodersbreakfast.lp4j.midi.MidiDeviceConfiguration;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;

public class DeviceDetector {
    public static final String DEVICE_SIGNATURE = "Launchpad";

    public static MidiDeviceConfiguration detectDevices() throws MidiUnavailableException {
        MidiDevice inputDevice = autodetectInputDevice();
        MidiDevice outputDevice = autodetectOutputDevice();
        return new MidiDeviceConfiguration(inputDevice, outputDevice);
    }

    private static List<MidiDevice> getMidiDevices() throws MidiUnavailableException {
        List<MidiDevice> devices = new ArrayList<>();
        MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info : midiDeviceInfo) {
            if (info.getDescription().contains(DEVICE_SIGNATURE) || info.getName().contains(DEVICE_SIGNATURE)) {
                devices.add(MidiSystem.getMidiDevice(info));
            }
        }
        return devices;
    }

    public static MidiDevice autodetectInputDevice() throws MidiUnavailableException {
        for (MidiDevice device : getMidiDevices()) {
            if (device.getMaxTransmitters() == -1) {
                device.close();
                return device;
            }
            device.close();
        }
        return null;
    }

    public static MidiDevice autodetectOutputDevice() throws MidiUnavailableException {
        for (MidiDevice device : getMidiDevices()) {
            if (device.getMaxReceivers() == -1) {
                device.close();
                return device;
            }
            device.close();
        }
        return null;
    }
}