package me.elia.lpdeck.launchpad;

import net.thecodersbreakfast.lp4j.midi.MidiDeviceConfiguration;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;

public class DeviceDetector {
    public static final String DEVICE_SIGNATURE = "Launchpad";

    public static MidiDeviceConfiguration detectDevices() throws MidiUnavailableException {
        MidiDevice inputDevice = detectInputDevice();
        MidiDevice outputDevice = detectOutputDevice();
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

    private static MidiDevice detectInputDevice() throws MidiUnavailableException {
        for (MidiDevice device : getMidiDevices()) {
            if (device.getMaxTransmitters() == -1) {
                device.close();
                return device;
            }
            device.close();
        }
        return null;
    }

    private static MidiDevice detectOutputDevice() throws MidiUnavailableException {
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