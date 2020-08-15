package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidiDevice;
import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MidiOut extends MidiDeviceMgr {
    protected final Logger logger = LoggerFactory.getLogger(MidiOut.class);
//    private final RtMidiLibrary lib = super.lib;

    public MidiOut() {
        int api = RtMidi.Api.UNSPECIFIED.getIntValue();
        super.rtMidiDevice = create(api, super.deviceName);
    }

    public MidiOut(int api, String deviceName) {

        if (!deviceName.isEmpty()) {

            // Remove the eventual semicolon form client name.
            // The semicolon is generally used as a separator between client and port name and id).
            deviceName = deviceName.replaceAll(":"," ");
            super.deviceName = deviceName;
        }
        super.rtMidiDevice = create(api, deviceName);
    }

    @Override
    public void close() {
        closeDevice();
        free();
    }

    /**
     *
     *
     */
    @Override
    public void free() {
        try {
            lib.rtmidi_out_free(rtMidiDevice);
            if (rtMidiDevice.ok == 0) throw new MidiException();
            logger.info(getDeviceClassName() + " memory ... freed");
        } catch (Throwable throwable) {

        }
    }

    /**
     *
     */
    @Override
    public int getCurrentApiId() {
        return lib.rtmidi_out_get_current_api(rtMidiDevice);
    }

    /**
     *
     */
    private RtMidiDevice create(int api, String clientName) {
        RtMidiDevice midiDevice = lib.rtmidi_out_create(api, clientName);
//        if (midiDevice.ok == 0) throw new MidiException(midiDevice);
        return midiDevice;
    }

    /**
     *
     */
    public int sendMessage(byte[] message, int length) {
        int result = 0;

        if (rtMidiDevice.ok != 0) {
            result = lib.rtmidi_out_send_message(rtMidiDevice, message, length);
        }
        else {
            System.out.println("No out device found - Received data cannot be sent!");
        }
        return result;
    }

    /**
     *
     */
    public int sendMessage(MidiMessage midiMessage) {

        int result = 0;

        if (rtMidiDevice.ok != 0) {
            try {
                result = lib.rtmidi_out_send_message(rtMidiDevice, midiMessage.getMidiData(), midiMessage.getMidiDataSize());
            } catch (MidiException me) {
                me.getMessage();
            }
        }
        else {
            System.out.println("No out device found - Received data cannot be sent!");
        }
        return result;
    }
}
