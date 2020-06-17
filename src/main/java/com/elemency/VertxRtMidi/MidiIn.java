package com.elemency.VertxRtMidi;

import com.elemency.VertxRtMidi.RtMidiLib.RtMidiLibrary;
import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.ochafik.lang.jnaerator.runtime.NativeSizeByReference;
import com.sun.jna.Callback;
import com.sun.jna.CallbackThreadInitializer;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class MidiIn extends MidiBase {
    protected final Logger logger = LoggerFactory.getLogger(MidiIn.class);
    private final RtMidiLibrary lib = super.lib;

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

    @Override
    public void close() throws Exception {
        cancelCallback();
        closePort();
        free();
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
    public int getCurrentApi() throws Exception {
        try {
            return lib.rtmidi_in_get_current_api(midiDevice);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public void setCallback(MidiInCallback callback, String threadName, Pointer userData) throws Exception {
        try {

            /**
             * The CallbackThreadInitializer ensures that the VM doesn't generate multiple Java Threads for the same
             * native thread. This must be done before attaching the thread to the VM.
             */
            CallbackThreadInitializer cti = new CallbackThreadInitializer(false, false, threadName);
            Native.setCallbackThreadInitializer(callback, cti);

            lib.rtmidi_in_set_callback(midiDevice, callback, userData);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public void cancelCallback() throws Exception {
        try {
            logger.info("Cancelling IN callback...");
            lib.rtmidi_in_cancel_callback(midiDevice);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public void ignoreTypes(MidiDevice device, byte midiSysex, byte midiTime, byte midiSense) {
        lib.rtmidi_in_ignore_types(device, midiSysex, midiTime, midiSense);
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

    /**
     * RtMidiIn callback function.
     * See ref RtMidiIn::RtMidiCallback.
     * <i>native declaration : /run/media/elemency/Data/Prjs/SandBox/Midi/RtMidi/rtmidi_c.h</i>
     */
    public interface MidiInCallback extends Callback {

        /**
         * @param timeStamp   The time at which the message has been received.
         * @param message     The midi message.
         * @param messageSize Size of the Midi message.
         * @param userData    Additional user data for the callback.
         */
        void process(double timeStamp, Pointer message, NativeSize messageSize, Pointer userData);
    }

}
