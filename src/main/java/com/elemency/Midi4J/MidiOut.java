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

import com.elemency.Midi4J.RtMidiDriver.RtMidiSysApiMgr;
import com.elemency.Midi4J.RtMidiDriver.RtMidiDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MidiOut extends MidiDevice {
    protected final Logger logger = LoggerFactory.getLogger(MidiOut.class);

    /**
     * MidiIn default constructor.
     */
    public MidiOut() {
        int api = RtMidiSysApiMgr.Api.UNSPECIFIED.getIntValue();
        super.rtMidiDevice = create(api, super.sourceDeviceName);
    }

    public MidiOut(int api, String sourceDeviceName) {

        if (!sourceDeviceName.isEmpty()) {

            // Remove the eventual semicolon from client name.
            // The semicolon is generally used as a separator between client and port name and id).
            sourceDeviceName = sourceDeviceName.replaceAll(":", " ");
            super.sourceDeviceName = sourceDeviceName;
        }
        super.rtMidiDevice = create(api, sourceDeviceName);
    }


    /**
     * Called on try with resources exception thrown.
     */
    @Override
    public void close() {
        closeSourceDevice();
        freeMemory();
    }

    /**
     * Free the native memory used by this source device instance.
     */
    @Override
    public void freeMemory() {
        if (rtMidiDevice == null) {
            throw new MidiException("This OUT device is null and its memory can't be freed.");
        }

        lib.rtmidi_out_free(rtMidiDevice);
        if (rtMidiDevice.ok == 0) throw new MidiException("");
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
            throw new NullPointerException("This OUT device is null - can't find out about its Api ID.");
        }
        return lib.rtmidi_out_get_current_api(rtMidiDevice);
    }

    /**
     * Create a new source midi OUT device based on selected midi api
     *
     * @param api               chosen available api
     * @param sourceDeviceName  name given to this new source midi device
     * @return                  RtMidiDevice
     */
    private RtMidiDevice create(int api, String sourceDeviceName) {

        return lib.rtmidi_out_create(api, sourceDeviceName);
    }

    /**
     * Send a midi message set into a byte[] to the driver
     *
     * @param message   midi message as byte[] to be sent
     * @param length    length of the midi message to be sent
     * @return          int
     */
    public int sendMessage(byte[] message, int length) {
        if (rtMidiDevice == null) {
            throw new NullPointerException("This OUT device is null... can't send messages.");
        }

        return lib.rtmidi_out_send_message(rtMidiDevice, message, length);
    }

    /**
     * Send a midi message set into MidiMessage instance to the driver
     *
     * @param midiMessage   midi message to be sent
     * @return              int
     */
    public int sendMessage(MidiMessage midiMessage) {

        if (rtMidiDevice == null) {
            throw new NullPointerException("This OUT device is null... can't send messages.");
        }

        return lib.rtmidi_out_send_message(rtMidiDevice, midiMessage.getMidiData(), midiMessage.getMidiDataSize());
    }
}
