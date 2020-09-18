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
import com.elemency.Midi4J.Broadcaster.BroadcastListener;
import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 * This sample illustrates how both a Broadcaster/Listener and a user Callback can be used
 * together to handle incoming messages from their respective native MidiIn source device.
 */
public class WithBoth extends KeepRunning implements BroadcastListener, AppOption {
    private final String sampleTitle = "Method with both Callback and Broadcaster";
    private final Logger logger = LoggerFactory.getLogger(WithBoth.class);
    private MidiIn midi4jIn = null;
    private MidiOut midi4jOut = null;
    private MidiIn midi4j2In = null;
    private MidiOut midi4j2Out = null;

    /**
     * Midi In BroadcastListener receiving all messages from both native MidiIn source devices.
     */
    @Override
    public void receiveMessage(UUID uuid, MidiMessage midiMessage, Pointer userData) {
        try {
            if (!doQuit) {
                if (midiMessage.getControllerNumber() == 89 && midiMessage.getControllerValue() == 127) {
                    logger.info("quitting...");
                    Broadcaster.unregister(this);
                    Broadcaster.shutdownBroadcaster();
                    doQuit();
                    return;
                }
            }

            // Message Routing (only needed when more than one device uses the broadcaster).
            if (midi4jIn.getSourceDeviceUUID().equals(uuid)) {
                midi4jOut.sendMessage(midiMessage);
            }

            logger.info(
                    SmpteTimecode.getTimecode(SmpteTimecode.getElapsedTimeSinceStartTime()) +
                            midiMessage.timeStampAsTimecode() + midiMessage.getDescription()
            );

        } catch (MidiException | NullPointerException | IllegalStateException me) {
                me.printStackTrace();
        }
    }

    /**
     * Midi In callback receiving messages from native MidiIn source device 2.
     */
    public final MidiIn.MidiInCallback fromMidi4j2In = (timeStamp, midiData, midiDataSize, userData) -> {

        try {
            /* Create a new MidiMessage (based on incoming native raw data) and
            sends it to our application. */
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

        System.out.println("---------------------------------------------");
        System.out.println("| " + sampleTitle + " |");
        System.out.println("---------------------------------------------\n");

        try (
                MidiOut midi4jOut = new MidiOut(RtMidi.Api.UNIX_JACK.getIntValue(), "Midi4J");
                MidiIn midi4jIn = new MidiIn(RtMidi.Api.LINUX_ALSA.getIntValue(), "Midi4J", 100, false);
                MidiOut midi4j2Out = new MidiOut(RtMidi.Api.UNIX_JACK.getIntValue(), "Midi4J2");
                MidiIn midi4j2In = new MidiIn(RtMidi.Api.LINUX_ALSA.getIntValue(), "Midi4J2", 100, true);
        ) {

            this.midi4jIn = midi4jIn;
            this.midi4jOut = midi4jOut;
            this.midi4j2In = midi4j2In;
            this.midi4j2Out = midi4j2Out;

            try {
                System.out.println("Target Out device count: " + this.midi4jIn.getTargetDeviceCount());
                System.out.println("Target In device count: " + this.midi4jOut.getTargetDeviceCount());

                // List all available target Alsa OUT devices.
                List<Map<String, String>> outDevices;
                outDevices= this.midi4jIn.listTargetDevices(false);
                this.midi4jIn.connect("IN", 2, true);
                this.midi4j2In.connect("IN", 2, true);

                // Register our listener app to the Broadcaster.
                Broadcaster.register(this);

                // Set midi4j2In callback.
                midi4j2In.setCallback(fromMidi4j2In, "midi4j2In", null);

                // List all available target Jack In devices.
                List<Map<String, String>> inDevices;
                inDevices = this.midi4jOut.listTargetDevices(false);
                this.midi4jOut.connect("OUT", 1, true);
                this.midi4j2Out.connect("OUT", 2, true);

                outDevices = this.midi4jIn.listTargetDevices(true);
                inDevices = this.midi4jOut.listTargetDevices(true);
                outDevices = this.midi4j2In.listTargetDevices(true);
                inDevices = this.midi4j2Out.listTargetDevices(true);

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
