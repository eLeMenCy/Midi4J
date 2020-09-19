/*
 * Copyright (C) 2020 - eLeMenCy, All Rights Reserved
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   A copy is also included in the downloadable source code package
 *   containing Midi4J, in file "LICENSE".
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.elemency.Midi4J.Examples;

import com.elemency.Midi4J.*;
import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This sample illustrates how midi messages can be gathered via a crude game loop (polling).
 */
public class WithLoop /*extends KeepRunning*/ implements AppOption {
    private final String sampleTitle = "Method with Game Loop";
    private final Logger logger = LoggerFactory.getLogger(WithLoop.class);
    private MidiIn midi4jIn = null;
    private MidiOut midi4jOut = null;
    private MidiMessage midiMessage = null;

    private boolean doQuit = false;



    @Override
    public void init() throws Exception {

        System.out.println("-------------------------");
        System.out.println("| " + sampleTitle + " |");
        System.out.println("-------------------------\n");

        try (
                MidiOut midi4jOut = new MidiOut(RtMidi.Api.UNIX_JACK.getIntValue(), "Midi4J");
                MidiIn midi4jIn = new MidiIn(RtMidi.Api.LINUX_ALSA.getIntValue(), "Midi4J", 100, true);
        ) {

            this.midi4jIn = midi4jIn;
            this.midi4jOut = midi4jOut;

            try {
                System.out.println("Target Out device count: " + this.midi4jIn.getTargetDeviceCount());
                System.out.println("Target In device count: " + this.midi4jOut.getTargetDeviceCount());

                // List all available target Alsa OUT devices.
                this.midi4jIn.listTargetDevices(false);

                // Attempt to connect source IN devices to its respective target OUT device counterpart.
                this.midi4jIn.connect("IN", 2, true);

                // List all available target Jack In devices.
                this.midi4jOut.listTargetDevices(false);

                // Attempt to connect source OUT devices to its respective target IN device counterpart.
                this.midi4jOut.connect("OUT", 1, true);

                // List connected target devices only.
                this.midi4jIn.listTargetDevices(true);
                this.midi4jOut.listTargetDevices(true);

            } catch (MidiException | NullPointerException me) {
                me.printStackTrace();
            }

            while (!doQuit) {
                processInput();
                update();
                render();
                Thread.sleep(1);
            }

        } catch (MidiException me) {
            logger.error(String.valueOf(me));
        }
    }


    private void processInput() {
        midiMessage = midi4jIn.getMessage();

        if (midiMessage != null) {

            if (!doQuit) {
                if (midiMessage.getControllerNumber() == 89 && midiMessage.getControllerValue() == 127) {
                    logger.info("quitting...");
                    doQuit = true;
                    return;
                }
            }
        }
    }

    private void update() {
        if (midiMessage != null) {

            midi4jOut.sendMessage(midiMessage);
        }
    }

    private void render() {
        if (midiMessage != null && !doQuit) {
            logger.info(
                    SmpteTimecode.getTimecode(SmpteTimecode.getElapsedTimeSinceStartTime()) +
                            midiMessage.timeStampAsTimecode() + midiMessage.getDescription()
            );
        }
    }

}
