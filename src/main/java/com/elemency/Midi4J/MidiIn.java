package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import com.elemency.Midi4J.RtMidiDriver.RtMidiDevice;
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


    public MidiIn() {
        int api = RtMidi.Api.UNSPECIFIED.getIntValue();
        super.rtMidiDevice = create(api, super.sourceDeviceName, 100);
    }

    public MidiIn(int api, String sourceDeviceName, int queueSizeLimit) {
        if (!sourceDeviceName.isEmpty()) {

            // Remove the eventual semicolon from client name.
            // The semicolon is generally used as a separator between client and port name and id).
            sourceDeviceName = sourceDeviceName.replaceAll(":", " ");
            super.sourceDeviceName = sourceDeviceName;
        }
        super.rtMidiDevice = create(api, super.sourceDeviceName, queueSizeLimit);
    }

    /**
     * Called on try with resources exception thrown.
     */
    @Override
    public void close() {
        cancelCallback();
        closeSourceDevice();
        freeMemory();
    }

    /**
     * Free the native memory used byt this source device instance.
     */
    @Override
    public void freeMemory() {
        if (rtMidiDevice == null) {
            throw new MidiException("This IN device is null and its memory can't be freed.");
        }

        lib.rtmidi_in_free(rtMidiDevice);
        logger.info(getSourceDeviceClassName() + " memory ... freed");
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
     * Create a new source midi IN device based on selected midi api
     *
     * @param api               chosen available api
     * @param sourceDeviceName  name given to this new source midi device
     * @return                  RtMidiDevice
     */
    private RtMidiDevice create(int api, String sourceDeviceName, int queueSizeLimit) {

        return lib.rtmidi_in_create(api, sourceDeviceName, queueSizeLimit);
    }

    /**
     * Set the midi in callback ready to receive midi message form the native driver.
     *
     * @param callback      callback method name
     * @param threadName    callback thread name - this appears in the log
     * @param userData      user specific info from native
     * @return              boolean
     */
    public boolean setCallback(MidiInCallback callback, String threadName, Pointer userData)  {

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
     * Cancel the callback
     *
     * @return boolean
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
     * Filter out specific native midi messages.
    /**
     *
     * @param midiStatus    midi message status byte to filter out
     * @param midiTime      midi message time to filter out
     * @param midiSense     midi message sensing to filter out
     */
    public void ignoreTypes(byte midiStatus, byte midiTime, byte midiSense) {

        if (rtMidiDevice == null) {
            throw new NullPointerException("This IN device is null - can't filter its incoming event types.");
        }

        lib.rtmidi_in_ignore_types(rtMidiDevice, midiStatus, midiTime, midiSense);
    }

    /**
     * Get midi message from native via a polling loop instead of a callback.
     *
     * @param message   ByteBuffer receiving the native raw midi message
     * @param size      midi message size
     * @return          time stamp
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
