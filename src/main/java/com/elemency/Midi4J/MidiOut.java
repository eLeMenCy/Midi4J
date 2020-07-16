package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidiDevice;
import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import com.elemency.Midi4J.RtMidiDriver.RtMidiLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MidiOut extends MidiBase {
    protected final Logger logger = LoggerFactory.getLogger(MidiOut.class);
    private final RtMidiLibrary lib = super.lib;

    public MidiOut() {
        int api = RtMidi.Api.UNSPECIFIED.getIntValue();
        super.rtMidiDevice = create(api, super.clientName);
    }

    public MidiOut(int api, String clientName) {

        if (!clientName.isEmpty()) {

            // Remove the eventual semicolon form client name.
            // The semicolon is generally used as a separator between client and port name and id).
            clientName = clientName.replaceAll(":"," ");
            super.clientName = clientName;
        }
        super.rtMidiDevice = create(api, clientName);
    }

    @Override
    public void close() {
        closeDevice();
        free();
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
}
