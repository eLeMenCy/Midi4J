package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidiDevice;
import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import com.elemency.Midi4J.examples.App;
import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.ochafik.lang.jnaerator.runtime.NativeSizeByReference;
import com.sun.jna.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class MidiIn extends MidiDeviceMgr {
    protected final Logger logger = LoggerFactory.getLogger(MidiIn.class);
    private App app = null;


    public MidiIn() {

    }

    public MidiIn(App app) /*throws MidiException*/ {
        this.app = app;
        int api = RtMidi.Api.UNSPECIFIED.getIntValue();
        super.rtMidiDevice = create(api, super.deviceName, 100);
        setCallback(fromNative, "native", null);
    }

    public MidiIn(int api, String deviceName, int queueSizeLimit, App app)/* throws MidiException*/ {
        this.app = app;
        if (!deviceName.isEmpty()) {

            // Remove the eventual semicolon from client name.
            // The semicolon is generally used as a separator between client and port name and id).
            deviceName = deviceName.replaceAll(":"," ");
            super.deviceName = deviceName;
        }
        super.rtMidiDevice = create(api, super.deviceName, queueSizeLimit);
        setCallback(fromNative, "native", null);
    }

    /**
     *
     */
    @Override
    public void close()/* throws MidiException*/ {
        cancelCallback();
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
            lib.rtmidi_in_free(rtMidiDevice);
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
        return lib.rtmidi_in_get_current_api(rtMidiDevice);
    }

    /**
     *
     */
    private RtMidiDevice create(int api, String clientName, int queueSizeLimit)/* throws MidiException*/ {

        RtMidiDevice midiDevice = lib.rtmidi_in_create(api, clientName, queueSizeLimit);
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

        lib.rtmidi_in_set_callback(rtMidiDevice, callback, userData);
//        if (midiDevice.ok == 0) throw new MidiException(midiDevice);
        SmpteTimecode.setStartTime();

        return rtMidiDevice.ok != 0;
    }

    /**
     *
     */
    public boolean cancelCallback() {
        logger.info("Cancelling IN callback...");
        lib.rtmidi_in_cancel_callback(rtMidiDevice);
//        if (midiDevice.ok == 0) throw new MidiException(midiDevice);
        return rtMidiDevice.ok != 0;
    }

    /**
     *
     */
    public void ignoreTypes(byte midiSysex, byte midiTime, byte midiSense) {
        lib.rtmidi_in_ignore_types(rtMidiDevice, midiSysex, midiTime, midiSense);
    }

    /**
     *
     */
    public double getMessage(ByteBuffer message, NativeSizeByReference size) {
        double midiMessage = lib.rtmidi_in_get_message(rtMidiDevice, message, size);
//        if (midiDevice.ok == 0) throw new MidiException(midiDevice);
        return midiMessage;
    }

    /**
     * Midi In interface callback from native.
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

    /**
     * Midi In raw callback implementation.
     *
     * @param timeStamp     The time at which the message has been received.
     * @param message       The midi message.
     * @param messageSize   Size of the Midi message.
     * @param userData      Additional user data.
     */
    public final MidiInCallback fromNative = (timeStamp, midiData, midiDataSize, userData) -> {

        MidiMessage midiMessage = new MidiMessage(midiData, midiDataSize, timeStamp);

//        try {
//            if (message == null) {
//                throw new MidiException("A midi Message object can't be null");
//            }
//
            // Send our MidiMessage (based on incoming raw data) to our application.
            this.app.processMidiInMessage(timeStamp, midiMessage, userData);

//        } catch (MidiException me) {
//            logger.error(me.getMessage());

//        } catch (NullPointerException npe) {
//            logger.error("Huuh" + npe);
//        }
    };
}
