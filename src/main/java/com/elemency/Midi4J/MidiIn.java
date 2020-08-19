package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import com.elemency.Midi4J.RtMidiDriver.RtMidiDevice;
import com.elemency.Midi4J.examples.App;
import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.ochafik.lang.jnaerator.runtime.NativeSizeByReference;
import com.sun.jna.Callback;
import com.sun.jna.CallbackThreadInitializer;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class MidiIn extends MidiDeviceMgr {
    protected final Logger logger = LoggerFactory.getLogger(MidiIn.class);
    private App app = null;
    /**
     * Midi In raw callback implementation.
     *
     * @param timeStamp     The time at which the message has been received.
     * @param message       The midi message.
     * @param messageSize   Size of the Midi message.
     * @param userData      Additional user data.
     */
    public final MidiInCallback fromNative = (timeStamp, midiData, midiDataSize, userData) -> {

        try {
            /* Create a new MidiMessage (based on incoming native raw data) and
            sends it to our application. */
            MidiMessage midiMessage = new MidiMessage(midiData, midiDataSize, timeStamp);
            this.app.processMidiInMessage(timeStamp, midiMessage, userData);

        } catch (MidiException | NullPointerException me) {
            me.printStackTrace();
        }
    };

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
            deviceName = deviceName.replaceAll(":", " ");
            super.deviceName = deviceName;
        }
        super.rtMidiDevice = create(api, super.deviceName, queueSizeLimit);
        setCallback(fromNative, "native", null);
    }

    /**
     * Called on try with resources exception thrown.
     */
    @Override
    public void close()/* throws MidiException*/ {
        cancelCallback();
        closeDevice();
        free();
    }

    /**
     *
     */
    @Override
    public void free() {
        if (rtMidiDevice == null) {
            throw new MidiException("This IN device is null and its memory can't be freed.");
        }

        lib.rtmidi_in_free(rtMidiDevice);
        logger.info(getDeviceClassName() + " memory ... freed");
    }

    /**
     *
     */
    @Override
    public int getCurrentApiId() {
        if (rtMidiDevice == null) {
            throw new MidiException("This IN device is null - can't find out about its Api ID.");
        }

        return lib.rtmidi_in_get_current_api(rtMidiDevice);
    }

    /**
     *
     */
    private RtMidiDevice create(int api, String clientName, int queueSizeLimit)/* throws MidiException*/ {

        return lib.rtmidi_in_create(api, clientName, queueSizeLimit);
    }

    /**
     *
     */
    public boolean setCallback(MidiInCallback callback, String threadName, Pointer userData) /*throws Exception*/ {

        if (rtMidiDevice == null) {
            throw new MidiException("This IN device is null - can't set its callback.");
        }

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

        if (rtMidiDevice == null) {
            throw new NullPointerException("This IN device is null - can't cancel is callback.");
        }

        logger.info("Cancelling IN callback...");
        lib.rtmidi_in_cancel_callback(rtMidiDevice);
//        if (midiDevice.ok == 0) throw new MidiException(midiDevice);
        return rtMidiDevice.ok != 0;
    }

    /**
     *
     */
    public void ignoreTypes(byte midiSysex, byte midiTime, byte midiSense) {

        if (rtMidiDevice == null) {
            throw new NullPointerException("This IN device is null - can't filter its incoming event types.");
        }

        lib.rtmidi_in_ignore_types(rtMidiDevice, midiSysex, midiTime, midiSense);
    }

    /**
     *
     */
    public double getMessage(ByteBuffer message, NativeSizeByReference size) {

        if (rtMidiDevice == null) {
            throw new NullPointerException("This IN device is null - can't poll raw midi messages.");
        }

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
}
