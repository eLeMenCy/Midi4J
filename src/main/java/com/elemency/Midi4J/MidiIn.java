package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import com.elemency.Midi4J.RtMidiDriver.RtMidiLibrary;
import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.ochafik.lang.jnaerator.runtime.NativeSizeByReference;
import com.sun.jna.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class MidiIn extends MidiBase {
    protected final Logger logger = LoggerFactory.getLogger(MidiIn.class);
    private final RtMidiLibrary lib = super.lib;

    public MidiIn() throws MidiException {
        int api = RtMidi.Api.UNSPECIFIED.getIntValue();
        super.midiDevice = create(api, super.clientName, 100);
    }

    public MidiIn(int api, String clientName, int queueSizeLimit) throws MidiException {
        if (!clientName.isEmpty()) {
            super.clientName = clientName;
        }
        super.midiDevice = create(api, super.clientName, queueSizeLimit);
    }

    @Override
    public void close()/* throws MidiException*/ {
        cancelCallback();
        closePort();
        free();
    }

    /**
     *
     */
    private MidiDevice create(int api, String clientName, int queueSizeLimit) throws MidiException {
        MidiDevice midiDevice = lib.rtmidi_in_create(api, clientName, queueSizeLimit);
        return midiDevice;
    }

    /**
     *
     */
    public boolean setCallback(MidiInCallback callback, String threadName, Pointer userData) /*throws Exception*/ {

        /**
         * The CallbackThreadInitializer ensures that the VM doesn't generate multiple Java Threads for the same
         * native thread. This must be done before attaching the thread to the VM.
         */
        CallbackThreadInitializer cti = new CallbackThreadInitializer(false, false, threadName);
        Native.setCallbackThreadInitializer(callback, cti);

        lib.rtmidi_in_set_callback(midiDevice, callback, userData);
//        if (midiDevice.ok == 0) throw new MidiException(midiDevice);
        return midiDevice.ok != 0;
    }

    /**
     *
     */
    public boolean cancelCallback() {
        logger.info("Cancelling IN callback...");
        lib.rtmidi_in_cancel_callback(midiDevice);
//        if (midiDevice.ok == 0) throw new MidiException(midiDevice);
        return midiDevice.ok != 0;
    }

    /**
     *
     */
    public void ignoreTypes(byte midiSysex, byte midiTime, byte midiSense) {
        lib.rtmidi_in_ignore_types(midiDevice, midiSysex, midiTime, midiSense);
    }

    /**
     *
     */
    public double getMessage(ByteBuffer message, NativeSizeByReference size) {
        double midiMessage = lib.rtmidi_in_get_message(midiDevice, message, size);
//        if (midiDevice.ok == 0) throw new MidiException(midiDevice);
        return midiMessage;
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
