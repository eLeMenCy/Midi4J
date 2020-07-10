package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import com.elemency.Midi4J.RtMidiDriver.RtMidiLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MidiOut extends MidiBase {
    protected final Logger logger = LoggerFactory.getLogger(MidiOut.class);
    private final RtMidiLibrary lib = super.lib;

    public MidiOut() {
        int api = RtMidi.Api.UNSPECIFIED.getIntValue();
        super.midiDevice = create(api, super.clientName);
    }

    public MidiOut(int api, String clientName) {
        if (!clientName.isEmpty()) {
            super.clientName = clientName;
        }
        super.midiDevice = create(api, clientName);
    }

    @Override
    public void close() {
        closePort();
        free();
    }

    /**
     *
     */
    private MidiDevice create(int api, String clientName) {
        MidiDevice midiDevice = lib.rtmidi_out_create(api, clientName);
//        if (midiDevice.ok == 0) throw new MidiException(midiDevice);
        return midiDevice;
    }

    /**
     *
     */
    public int sendMessage(byte[] message, int length) {
        int result = 0;

        if (midiDevice.ok != 0) {
            result = lib.rtmidi_out_send_message(midiDevice, message, length);
        }
        else {
            System.out.println("No out device found - Received data cannot be sent!");
        }
        return result;
    }
}
