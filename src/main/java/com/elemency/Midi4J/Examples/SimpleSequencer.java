package com.elemency.Midi4J.Examples;

import com.elemency.Midi4J.MidiException;
import com.elemency.Midi4J.MidiMessage;
import com.elemency.Midi4J.MidiOut;
import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * inspired from Sand, software and sound
 * http://sandsoftwaresound.net/source/arduino-project-source/simple-midi-sequencer/
 */
public class SimpleSequencer extends KeepRunning {
    private final Logger logger = LoggerFactory.getLogger(SimpleSequencer.class);
    // Midi Out device which will send our messages to an available target Midi In Device.
    private MidiOut midi4jOut = null;

    //----- DO NOT CHANGE -----
    private final int REST = -1;
    // Loops the sequence twice (default).
    private int loopAmount = 2;
    private int seqStep = 0;
    private int note = REST;
    private Timer t = new Timer();
    private int loopIndex = 0;


    // ----- CHANGE TO YOUR HEART CONTENT -----
    private final int CHANNEL = 1;
    private final int VELOCITY = 80;
    private final int TEMPO = 110;
    // Alan Walker - Faded (Intro)
    private final int[][] SEQUENCE = {
    // Note {number, duration}
            {65, 4},                // F3
            {65, 4},                // F3
            {65, 4},                // F3
            {69, 4},                // A3
            {74, 4},                // D4
            {74, 4},                // D4
            {74, 4},                // D4
            {72, 4},                // C4
            {69, 4},                // A3
            {69, 4},                // A3
            {69, 4},                // A3
            {69, 4},                // A3
            {64, 4},                // E3
            {64, 4},                // E3
            {64, 4},                // E3
            {62, 4},                // D3
            {65, 4},                // F3
            {65, 4},                // F3
            {65, 4},                // F3
            {69, 4},                // A3
            {74, 4},                // D4
            {74, 4},                // D4
            {74, 4},                // D4
            {77, 8},                // C4
            {74, 8},                // C4
            {72, 4},                // A3
            {69, 4},                // A3
            {69, 4},                // A3
            {69, 4},                // A3
            {64, 4},                // E3
            {64, 4},                // E3
            {64, 4},                // E3
            {62, 4}                 // D3
    };


    public class Task extends TimerTask {

        private void playMidiNote(int velocity) {
            MidiMessage midiMessage;

            if (note > -1) {
                midiMessage = MidiMessage.noteOn(CHANNEL, note, velocity, 0);
                midi4jOut.sendMessage(midiMessage);
                logger.info(midiMessage.getDescription());
            }
        }

        @Override
        public void run() {

            playMidiNote(0);

            note = SEQUENCE[seqStep][0];
            long duration = (int)((float)60 * 4 / SEQUENCE[seqStep][1] / TEMPO * 1000);

            if (loopIndex == loopAmount) {
                t.cancel();
                doQuit();
                return;
            }

            Random random = new Random();
            int velocity = random.nextInt(127);

            playMidiNote(VELOCITY + random.nextInt(127 - VELOCITY));

            if (++seqStep > SEQUENCE.length - 1) {
                loopIndex ++;
                seqStep = 0;
            }

            t.schedule(new Task(), duration);
        }
    }

    @Override
    protected void init() throws Exception {

        try (
                MidiOut midi4jOut = new MidiOut(RtMidi.Api.UNIX_JACK.getIntValue(), "Midi4J")
        ) {
            this.midi4jOut = midi4jOut;

            try {
                System.out.println("Target In device count: " + this.midi4jOut.getTargetDeviceCount());

                this.midi4jOut.listTargetDevices(false);
                this.midi4jOut.connect("OUT", 2, true);

                this.midi4jOut.listTargetDevices(false);

            } catch (MidiException | NullPointerException me) {
                me.printStackTrace();
            }

            loopAmount = 2;

            // Start playing the sequence within 1 second.
            t.schedule(new Task(), 1000);

            // Keep our application going.
            keepRunning();

        } catch (MidiException me) {
            logger.error(String.valueOf(me));
        }
    }
}
