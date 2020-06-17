package com.elemency.VertxRtMidi;

import com.elemency.VertxRtMidi.RtMidiLib.RtMidiLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MidiOut extends MidiBase{
    private final RtMidiLibrary lib = super.lib;
    protected final Logger logger = LoggerFactory.getLogger(MidiOut.class);

    public MidiOut() throws Exception {
        try {
            super.midiDevice = createDefault();
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    public MidiOut(int api, String clientName) throws Exception {
        try {
            super.midiDevice = create(api, clientName);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    @Override
    public void close() throws Exception {
        closePort();
        free();
    }

    /**
     *
     */
    private MidiDevice createDefault() throws Exception {
        try {
            return lib.rtmidi_out_create_default();
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    private MidiDevice create(int api, String clientName) throws Exception {
        try {
            return lib.rtmidi_out_create(api, clientName);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public int getCurrentApi() throws Exception {
        try {
            return lib.rtmidi_out_get_current_api(midiDevice);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public int sendMessage(byte[] message, int length) throws Exception {
        try {
            return lib.rtmidi_out_send_message(midiDevice, message, length);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

}
