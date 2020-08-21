package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MidiDeviceMgrTest {

    private MidiIn midi4jIn = null;
    private MidiOut midi4jOut = null;


    @BeforeEach
    void init()
    {
        MidiOut midi4jOut = new MidiOut(RtMidi.Api.UNIX_JACK.getIntValue(), "Midi4J");
        MidiIn midi4jIn = new MidiIn(RtMidi.Api.LINUX_ALSA.getIntValue(), "Midi4J", 100, null);
        this.midi4jIn = midi4jIn;
        this.midi4jOut = midi4jOut;
    }

    @AfterEach
    void clearup() {
        midi4jIn.close();
        midi4jOut.close();
    }

    @Test
    @Disabled
    void getRtMidiDevice() {

    }

    @Test
    void getDeviceClassName() {
        assertEquals("MidiOut", midi4jOut.getSourceDeviceClassName());
        assertEquals("MidiIn", midi4jIn.getSourceDeviceClassName());
    }

    @Test
    void getTargetDeviceType() {
        assertEquals("In", midi4jOut.getTargetDeviceType());
        assertEquals("Out", midi4jIn.getTargetDeviceType());
    }

    @Test
    void getDeviceType() {
        assertEquals("Out", midi4jOut.getSourceDeviceType());
        assertEquals("In", midi4jIn.getSourceDeviceType());
    }

    @Test
    @Disabled
    void error() {
    }

    @Test
    void getCurrentApiId() {
        assertEquals(3, midi4jOut.getCurrentApiId());
        assertEquals(2, midi4jIn.getCurrentApiId());
    }

    @Test
    void getCurrentApiName() {
        assertEquals("Jack", midi4jOut.getCurrentApiName());
        assertEquals("ALSA", midi4jIn.getCurrentApiName());
    }

    @Test
    void getTargetDeviceName() {
        assertEquals("Calf Studio Gear", midi4jOut.getTargetDeviceName(1));
        assertEquals("FCA1616", midi4jIn.getTargetDeviceName(1));
    }

    @Test
    void getDeviceName() {
        assertEquals("Midi4J", midi4jOut.getSourceDeviceName());
        assertEquals("Midi4J", midi4jIn.getSourceDeviceName());
    }

    @Test
    void setDeviceName() {
        midi4jOut.setSourceDeviceName("DeviceOUT");
        midi4jIn.setSourceDeviceName("DeviceIN");
        assertEquals("DeviceOUT", midi4jOut.getSourceDeviceName());
        assertEquals("DeviceIN", midi4jIn.getSourceDeviceName());
    }

    @Test
    void getPortName() {
        midi4jOut.setSourcePortName("");
        midi4jIn.setSourcePortName("");
        assertEquals("OUT", midi4jOut.getSourcePortName());
        assertEquals("IN", midi4jIn.getSourcePortName());

        midi4jOut.setSourcePortName("PortOUT");
        midi4jIn.setSourcePortName("PortIN");
        assertEquals("PortOUT", midi4jOut.getSourcePortName());
        assertEquals("PortIN", midi4jIn.getSourcePortName());
    }

    @Test
    void setPortNameEmpty() {
        midi4jOut.setSourcePortName("");
        midi4jIn.setSourcePortName("");
        assertEquals("OUT", midi4jOut.getSourcePortName());
        assertEquals("IN", midi4jIn.getSourcePortName());
    }
    @Test
    void setPortName() {
        midi4jOut.setSourcePortName("PortOUT");
        midi4jIn.setSourcePortName("PortIN");
        assertEquals("PortOUT", midi4jOut.getSourcePortName());
        assertEquals("PortIN", midi4jIn.getSourcePortName());
    }

    @Test
    void isDeviceOpen() {
        assertFalse(midi4jOut.isSourceDeviceOpen());
        assertFalse(midi4jIn.isSourceDeviceOpen());
    }

    @Test
    @Disabled
    void free() {
    }

    @Test
    void connectWithAutoConnect() {
        assertTrue(midi4jOut.connect("OUT", 1, true));
        assertTrue(midi4jIn.connect("IN", 2, true));
    }

    @Test
    void connectWithoutAutoConnect() {
        assertTrue(midi4jOut.connect("OUT", 1, false));
        assertTrue(midi4jIn.connect("IN", 2, false));
    }

    @Test
    @Disabled
    void openVirtualDevice() {
    }

    @Test
    @Disabled
    void getDeviceCount() {

    }

    @Test
    @Disabled
    void getFullDeviceDetails() {
    }

    @Test
    void getTargetPortName() {
        assertEquals("Organ MIDI In", midi4jOut.getTargetPortName(1));
        assertEquals("FCA1616 MIDI 1", midi4jIn.getTargetPortName(1));
    }

    @Test
    @Disabled
    void listTargetDevices() {

    }

    @Test
    @Disabled
    void closeDevice() {
    }
}