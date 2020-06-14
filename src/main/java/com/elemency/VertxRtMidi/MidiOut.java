package com.elemency.VertxRtMidi;

import com.elemency.VertxRtMidi.RtMidiLib.RtMidiLibrary;

public class MidiOut extends MidiBase {
    private final RtMidiLibrary lib = RtMidiLibrary.INSTANCE;

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

    /**
     *
     */
    @Override
    public MidiDevice getMidiDevice() {
        return super.midiDevice;
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
    public void free(MidiDevice device) throws Exception {
        try {
            lib.rtmidi_out_free(device);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public int getCurrentApi(MidiDevice device) throws Exception {
        try {
            return lib.rtmidi_out_get_current_api(device);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public int sendMessage(MidiDevice device, byte[] message, int length) throws Exception {
        try {
            return lib.rtmidi_out_send_message(device, message, length);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }
}
