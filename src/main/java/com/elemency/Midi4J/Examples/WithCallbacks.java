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

package com.elemency.Midi4J.Examples;

import com.elemency.Midi4J.*;
import com.elemency.Midi4J.RtMidiDriver.RtMidiSysApiMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This sample illustrates how user callbacks (one per source device) are set to handle
 * incoming messages from our 2 native MidiIn source devices respectively.
 */
public class WithCallbacks extends KeepRunning implements AppOption {
    private final String SAMPLE_TITLE = "Method with User Callbacks";
    private final Logger logger = LoggerFactory.getLogger(WithCallbacks.class);
    private MidiIn midi4jIn = null;
    private MidiOut midi4jOut = null;
    private MidiIn midi4j2In = null;
    private MidiOut midi4j2Out = null;

    /**
     * Midi In callback 1 receiving messages from native MidiIn source device 1.
     */
    public final MidiIn.MidiInCallback fromMidi4jIn = (timeStamp, midiData, midiDataSize, userData) -> {

        try {
            /* Create a new MidiMessage (based on incoming native raw data) and
            sends it. */
            MidiMessage midiMessage = new MidiMessage(midiData, midiDataSize, timeStamp);

            if (!doQuit) {
                if (midiMessage.getControllerNumber() == 89 && midiMessage.getControllerValue() == 127) {
                    logger.info("quitting...");
                    doQuit();
                    return;
                }
            }

            midi4jOut.sendMessage(midiMessage);

            logger.info(
                    SmpteTimecode.getTimecode(SmpteTimecode.getElapsedTimeSinceStartTime()) +
                            midiMessage.timeStampAsTimecode() + midiMessage.getDescription()
            );

        } catch (MidiException | NullPointerException me) {
                me.printStackTrace();
        }
    };

    /**
     * Midi In callback 2 receiving messages from native MidiIn source device 2.
     */
    public final MidiIn.MidiInCallback fromMidi4j2In = (timeStamp, midiData, midiDataSize, userData) -> {

        try {
            /* Create a new MidiMessage (based on incoming native raw data) and
            sends it. */
            MidiMessage midiMessage = new MidiMessage(midiData, midiDataSize, timeStamp);

            midi4j2Out.sendMessage(midiMessage);

            logger.info(
                    SmpteTimecode.getTimecode(SmpteTimecode.getElapsedTimeSinceStartTime()) +
                            midiMessage.timeStampAsTimecode() + midiMessage.getDescription()
            );

        } catch (MidiException | NullPointerException me) {
            me.printStackTrace();
        }
    };

    @Override
    public void init() throws Exception {

        System.out.println("------------------------------");
        System.out.println("| " + SAMPLE_TITLE + " |");
        System.out.println("------------------------------\n");

        try (
                MidiOut midi4jOut = new MidiOut(RtMidiSysApiMgr.Api.UNIX_JACK.getIntValue(), "Midi4J");
                MidiIn midi4jIn = new MidiIn(RtMidiSysApiMgr.Api.LINUX_ALSA.getIntValue(), "Midi4J", 100, true);
                MidiOut midi4j2Out = new MidiOut(RtMidiSysApiMgr.Api.UNIX_JACK.getIntValue(), "Midi4J2");
                MidiIn midi4j2In = new MidiIn(RtMidiSysApiMgr.Api.LINUX_ALSA.getIntValue(), "Midi4J2", 100, true)
        ) {

            this.midi4jIn = midi4jIn;
            this.midi4jOut = midi4jOut;
            this.midi4j2In = midi4j2In;
            this.midi4j2Out = midi4j2Out;

            try {
                System.out.println("Target Out device count: " + this.midi4jIn.getTargetDeviceCount());
                System.out.println("Target In device count: " + this.midi4jOut.getTargetDeviceCount());

                // List all available target Alsa OUT devices.
                this.midi4jIn.listTargetDevices(false);

                // Attempt to connect a source IN device to a target OUT device.
                this.midi4jIn.connect("IN", 2, true);
                this.midi4j2In.connect("IN", 2, true);

                // Set both In source device callbacks.
                midi4jIn.setCallback(fromMidi4jIn, "midi4jIn", null);
                midi4j2In.setCallback(fromMidi4j2In, "midi4j2In", null);

                // List all available target Jack In devices.
                this.midi4jOut.listTargetDevices(false);

                // Attempt to connect a source OUT device to a target IN device.
                this.midi4jOut.connect("OUT", 1, true);
                this.midi4j2Out.connect("OUT", 2, true);

                // List connected target devices only.
                this.midi4jIn.listTargetDevices(true);
                this.midi4jOut.listTargetDevices(true);
                this.midi4j2In.listTargetDevices(true);
                this.midi4j2Out.listTargetDevices(true);

            } catch (MidiException | NullPointerException me) {
                me.printStackTrace();
            }

            // Keep our application going.
            keepRunning();

        } catch (MidiException me) {
            logger.error(String.valueOf(me));
        }
    }
}
