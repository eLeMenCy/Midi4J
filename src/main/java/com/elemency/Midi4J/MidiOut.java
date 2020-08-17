package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import com.elemency.Midi4J.RtMidiDriver.RtMidiDevice;
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
            deviceName = deviceName.replaceAll(":", " ");
            super.deviceName = deviceName;
        }
        super.rtMidiDevice = create(api, deviceName);
    }


    /**
     * Called on try with resources exception thrown.
     */
    @Override
    public void close() {
        closeDevice();
        free();
    }

    /**
     *
     */
    @Override
    public void free() {
        if (rtMidiDevice == null) {
            throw new MidiException("This OUT device is null and its memory can't be freed.");
        }

        try {
            lib.rtmidi_out_free(rtMidiDevice);
            if (rtMidiDevice.ok == 0) throw new MidiException("");
            logger.info(getDeviceClassName() + " memory ... freed");
        } catch (Throwable throwable) {

        }
    }

    /**
     *
     */
    @Override
    public int getCurrentApiId() {

        if (rtMidiDevice == null) {
            throw new MidiException("This OUT device is null - can't find out about its Api ID.");
        }
        return lib.rtmidi_out_get_current_api(rtMidiDevice);
    }

    /**
     *
     */
    private RtMidiDevice create(int api, String clientName) {

        return lib.rtmidi_out_create(api, clientName);
    }

    /**
     *
     */
    public int sendMessage(byte[] message, int length) {
        if (rtMidiDevice == null) {
            throw new MidiException("This OUT device is null and - send messages.");
        }

        return lib.rtmidi_out_send_message(rtMidiDevice, message, length);
    }

    /**
     *
     */
    public int sendMessage(MidiMessage midiMessage) {

        if (rtMidiDevice == null) {
            throw new MidiException("This OUT device is null - can't send messages.");
        }

        return lib.rtmidi_out_send_message(rtMidiDevice, midiMessage.getMidiData(), midiMessage.getMidiDataSize());
    }
}
