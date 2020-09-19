/*
 * Copyright (C) 2020 - eLeMenCy, All Rights Reserved
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import com.elemency.Midi4J.RtMidiDriver.RtMidiDevice;
import com.elemency.Midi4J.RtMidiDriver.RtMidiLibrary.size_t;
import com.elemency.Midi4J.RtMidiDriver.RtMidiLibrary.size_tByReference;
import com.sun.jna.Callback;
import com.sun.jna.CallbackThreadInitializer;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MidiIn extends MidiDevice {
    private final Logger logger = LoggerFactory.getLogger(MidiIn.class);
    private boolean hasCallback = false;

    /**
     * MidiIn simple constructor.
     *
     * @param withUserCallback  boolean
     *                      Set to 'true':
     *                          Allows users to set the MidiIn callback method directly in their applications
     *                          (One callback per MidiIn source device instances created).
     *
     *                      Set to 'false':
     *                          The default callback method, internal to this class, is automatically used.
     *                          The user must implement the 'broadcasterListener' interface and override
     *                          the 'receiveMessage' method where all MidiIn messages of all MidiIn source
     *                          device instances will be sent.
     */
    public MidiIn(boolean withUserCallback) {
        int api = RtMidi.Api.UNSPECIFIED.getIntValue();
        super.rtMidiDevice = create(api, super.sourceDeviceName, 100);

        if (!withUserCallback) {
            String threadName = "native-" + sourceDeviceName;
            setCallback(fromNative, threadName, null);
        }
    }

    /**
     *
     * @param api               The Api id (0= Unknown, 1=CoreMidi, 2=ALSA, 3=JACK, 4=Winmm, 5=Dummy)
     * @param sourceDeviceName  The name of this Midi source instance.
     * @param queueSizeLimit    Maximum buffer size.
     * @param withUserCallback  boolean
     *                          Set to 'true':
     *                              Allows users to set the MidiIn callback method directly in their applications
     *                              (One callback per MidiIn source device instances created).
     *
     *                          Set to 'false':
     *                              The default callback method, internal to this class, is automatically used.
     *                              The user must implement the 'broadcasterListener' interface and override
     *                              the 'receiveMessage' method to which all MidiIn messages of all MidiIn source
     *                              device instances will be sent.
     */
    public MidiIn(int api, String sourceDeviceName, int queueSizeLimit, boolean withUserCallback) {
        if (!sourceDeviceName.isEmpty()) {

            // Remove the eventual semicolon from client name.
            // The semicolon is generally used as a separator between client and port name and id).
            sourceDeviceName = sourceDeviceName.replaceAll(":", " ");
            super.sourceDeviceName = sourceDeviceName;
        }
        super.rtMidiDevice = create(api, super.sourceDeviceName, queueSizeLimit);

        if (!withUserCallback) {
            String threadName = "native-" + sourceDeviceName;
            setCallback(fromNative, threadName, null);
        }
    }

    /**
     * Called when a try with resources exception is thrown.
     */
    @Override
    public void close() {
        cancelCallback();
        closeSourceDevice();
        freeMemory();
    }

    /**
     * Free the native memory used by this source device instance.
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
     * Return the API ID of the current MidiIn device instance.
     *
     * @return int
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
     * Set the midi in callback ready to receive midi message from the native driver.
     *
     * @param callback      callback method name
     * @param threadName    callback thread name - this appears in the log
     * @param userData      user specific info from native
     * @return              boolean
     */
    public void setCallback(MidiInCallback callback, String threadName, Pointer userData)  {

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
        SmpteTimecode.setStartTime();

        hasCallback = true;
    }

    /**
     * Cancel the callback
     *
     * @return boolean
     */
    public void cancelCallback() {

        if (!hasCallback) return;

        if (rtMidiDevice == null) {
            throw new NullPointerException("This IN device is null - can't cancel its callback.");
        }

        logger.info("Cancelling IN callback...");
        lib.rtmidi_in_cancel_callback(rtMidiDevice);
        hasCallback = false;
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
     * @return      MidiMessage or null.
     */
    public MidiMessage getMessage() {

        if (rtMidiDevice == null) {
            throw new NullPointerException("This IN device is null - can't poll raw midi messages.");
        }

        PointerByReference message = new PointerByReference();
        size_tByReference size = new size_tByReference();
        MidiMessage midiMessage = null;

        double timesStamp = lib.rtmidi_in_get_message(rtMidiDevice, message, size);

        if (size.getValue().intValue() > 0) {
            midiMessage = new MidiMessage(message.getPointer(), size.getValue(), timesStamp);
        }

        return midiMessage;
    }

    /**
     * Midi In interface callback from native.
     * See ref RtMidiIn::RtMidiCallback.
     * <i>native declaration : rtmidi_c.h</i>
     */
    public interface MidiInCallback extends Callback {

        /**
         * @param timeStamp   The time at which the message has been received.
         * @param message     The midi message.
         * @param messageSize Size of the Midi message.
         * @param userData    Additional user data for the callback.
         */
        void process(double timeStamp, Pointer message, size_t messageSize, Pointer userData);
    }

    /**
     * Midi In callback from native implementation.
     */
    public final MidiInCallback fromNative = (timeStamp, midiData, midiDataSize, userData) -> {

        try {
            /* Create a new MidiMessage (based on incoming native raw data) and
            sends it to our application. */
            MidiMessage midiMessage = new MidiMessage(midiData, midiDataSize, timeStamp);
            Broadcaster.broadcast(uuid, midiMessage, userData);

        } catch (MidiException | NullPointerException me) {
            me.printStackTrace();
        }
    };
}
