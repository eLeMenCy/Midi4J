package com.elemency.VertxRtMidi;

import com.elemency.VertxRtMidi.RtMidiLib.RtMidiLibrary;
import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.ochafik.lang.jnaerator.runtime.NativeSizeByReference;
import com.sun.jna.Callback;
import com.sun.jna.CallbackThreadInitializer;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import java.nio.ByteBuffer;

public class MidiIn extends MidiBase {
    private final RtMidiLibrary lib = RtMidiLibrary.INSTANCE;

    public MidiIn() throws Exception {
        try {
            super.midiDevice = createDefault();
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    public MidiIn(int api, String clientName, int queueSizeLimit) throws Exception {
        try {
            super.midiDevice = create(api, clientName, queueSizeLimit);
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
     * RtMidiIn callback function.
     * See ref RtMidiIn::RtMidiCallback.
     * <i>native declaration : /run/media/elemency/Data/Prjs/SandBox/Midi/RtMidi/rtmidi_c.h</i>
     */
    public interface MidiInCallback extends Callback {
        /**
         * @param timeStamp     The time at which the message has been received.
         * @param message       The midi message.
         * @param messageSize   Size of the Midi message.
         * @param userData      Additional user data for the callback.
         */
        void process(double timeStamp, Pointer message, NativeSize messageSize, Pointer userData);
    }

    /**
     *
     */
    private MidiDevice createDefault() throws Exception {
        try {
            return lib.rtmidi_in_create_default();
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    private MidiDevice create(int api, String clientName, int queueSizeLimit) throws Exception {
        try {
            return lib.rtmidi_in_create(api, clientName, queueSizeLimit);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public void free(MidiDevice device) throws Exception {
        try {
            lib.rtmidi_in_free(device);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public int getCurrentApi(MidiDevice device) throws Exception {
        try {
            return lib.rtmidi_in_get_current_api(device);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public void setCallback(MidiDevice device, MidiInCallback callback, String threadName, Pointer userData) {

        /**
         * The CallbackThreadInitializer ensures that the VM doesn't generate multiple Java Threads for the same
         * native thread. This must be done before attaching the thread to the VM.
         */
        CallbackThreadInitializer cti = new CallbackThreadInitializer(false,false, threadName);
        Native.setCallbackThreadInitializer(callback, cti);

        lib.rtmidi_in_set_callback(device, callback, userData);
    }


    /**
     *
     */
    public void cancelCallback(MidiDevice device) throws Exception {
        try {
            lib.rtmidi_in_cancel_callback(device);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public void ignoreTypes(MidiDevice device, byte midiSysex, byte midiTime, byte midiSense) throws Exception {
        try {
            lib.rtmidi_in_ignore_types(device, midiSysex, midiTime, midiSense);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public double getMessage(MidiDevice device, ByteBuffer message, NativeSizeByReference size) throws Exception {
        try {
            return lib.rtmidi_in_get_message(device, message, size);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

}
