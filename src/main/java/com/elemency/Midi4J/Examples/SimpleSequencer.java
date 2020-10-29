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

import com.elemency.Midi4J.MidiException;
import com.elemency.Midi4J.MidiMessage;
import com.elemency.Midi4J.MidiOut;
import com.elemency.Midi4J.RtMidiDriver.RtMidiSysApiMgr;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Simple sequencer inspired from
 * <a href="http://sandsoftwaresound.net/source/arduino-project-source/simple-midi-sequencer/">Sand, software and sound code</a><br><br>
 *
 * The sequence is the intro of one of my favourite tracks by Alan Walker
 * <a href="https://www.youtube.com/watch?v=60ItHLz5WEA"> - Faded - </a><br>
 *
 * Obviously the crude monodic sequence definitively doesn't do justice to the original but
 * demonstrates how simple it can be to send midi messages in Midi4J to any connected target device(s) using a single timer.<br><br>
 *
 * KNOWN ISSUE:<br>
 * Last note sticking when aborting the application while the sequence plays.<br>
 * I have yet to find a way to elegantly shutdown all running threads when hot stopping a console application.<br>
 * The best way right now is to let the application go through its
 * full cycle (2 loops) until it quits by itself<br>- Any suggestions welcome! -
 */
public class SimpleSequencer extends KeepRunning implements AppOption {
    private final String SAMPLE_TITLE = "Simple Sequencer";
    private final Logger logger = LoggerFactory.getLogger(SimpleSequencer.class);
    // Midi Out device which will send our messages to an available target Midi In Device.
    private MidiOut midi4jOut = null;

    //----- DO NOT CHANGE -----
    private final int REST = -1;
    // Loops the sequence twice (default).
    private int seqStep = 0;
    private int note = REST;
    private final Timer t = new Timer();
    private int loopIndex = 0;
    private List<Note> sequence;

    // ----- CHANGE TO YOUR HEART CONTENT -----
    private final int CHANNEL = 1;
    private final int VELOCITY = 80;
    private final int TEMPO = 110;
    // The number of time the sequence will repeat before quitting.
    private final int LOOP_AMOUNT = 2;


    // Sequencer engine
    public class SequencerEngine extends TimerTask {

        private void playMidiNote(int velocity) {
            MidiMessage midiMessage;

            if (note > -1) {
                midiMessage = MidiMessage.noteOn(CHANNEL, note, velocity, 0);
                midi4jOut.sendMessage(midiMessage);
                logger.info("(" + seqStep + ") " + midiMessage.getDescription());
            }
        }

        @Override
        public void run() {

            // Note duration has expired send a key up message (note OFF) to driver.
            playMidiNote(0);

            // Set new note and new duration
            note = sequence.get(seqStep).noteNumber;
            long duration = (int)((float)60 * 4 / sequence.get(seqStep).noteDuration / TEMPO * 1000);

            // Loop amount reached -> quit the application.
            if ((loopIndex) == LOOP_AMOUNT) {
                t.cancel();
                doQuit();
                return;
            }

            // Get some random velocity with offset and send new note to driver.
            Random random = new Random();
            playMidiNote(VELOCITY + random.nextInt(127 - VELOCITY));

            // Increment sequence to next step
            if ((seqStep ++) == sequence.size() - 1) {
                loopIndex ++ ;
                seqStep = 0;
            }

            // Reschedule timer with next note duration;
            t.schedule(new SequencerEngine(), duration);
        }
    }

    private static class Note {
        private String noteName = "C3";
        private int noteNumber = 60;
        private int noteDuration = 4;

        public String getNoteName() {
            return noteName;
        }

        public void setNoteName(String noteName) {
            this.noteName = noteName;
        }

        public int getNoteNumber() {
            return noteNumber;
        }

        public void setNoteNumber(int noteNumber) {
            this.noteNumber = noteNumber;
        }

        public int getNoteDuration() {
            return noteDuration;
        }

        public void setNoteDuration(int noteDuration) {
            this.noteDuration = noteDuration;
        }

        @Override
        public String toString() {
            return "Note{" +
                    "name='" + noteName + '\'' +
                    ", value=" + noteNumber +
                    ", duration=" + noteDuration +
                    '}';
        }
    }

    @Override
    public void init() throws Exception {

        System.out.println("--------------------");
        System.out.println("| " + SAMPLE_TITLE + " |");
        System.out.println("--------------------\n");

        try (
                MidiOut midi4jOut = new MidiOut(RtMidiSysApiMgr.Api.UNIX_JACK.getIntValue(), "Midi4J")
        ) {
            this.midi4jOut = midi4jOut;

            try {
                System.out.println("Target In device count: " + this.midi4jOut.getTargetDeviceCount());

                // List all available target Jack In devices.
                this.midi4jOut.listTargetDevices(false);

                // Attempt to connect a source OUT device to a target IN device.
                this.midi4jOut.connect("OUT", 2, true);

                // List connected target device only.
                this.midi4jOut.listTargetDevices(true);

                // Load Sequence into memory from a Json file.
                String fileName = "sequence.json";
                ClassLoader classLoader = getClass().getClassLoader();
                try {
                    InputStream inputStream = classLoader.getResourceAsStream(fileName);
                    sequence = new ObjectMapper().readValue(inputStream, new TypeReference<List<Note>>() {});
                } catch (IllegalArgumentException iae) {
                    logger.error("File '"+ fileName + "' doesn't exist - quitting...");
                    t.cancel();
                    doQuit();
                }

                displayTimecode = false;

            } catch (MidiException | NullPointerException me) {
                me.printStackTrace();
            }

            // Start playing the sequence within 1 second.
            t.schedule(new SequencerEngine(), 1000);

            // Keep our application going.
            keepRunning();

        } catch (MidiException me) {
            logger.error(String.valueOf(me));
        }
    }
}
