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
 * Simple sequencer inspired from
 * <a href="http://sandsoftwaresound.net/source/arduino-project-source/simple-midi-sequencer/">Sand,software and sound code</a><br><br>
 *
 * I have set the sequence to the intro of one of my favourite track by
 * Alan Walker - <a href="https://www.youtube.com/watch?v=60ItHLz5WEA">Faded</a><br>
 *
 * Obviously the monodic sequence definitively doesn't do justice to the original but
 * demonstrates how simple it can be to send midi messages to any connected device(s).<br><br>
 *
 * KNOWN ISSUE:<br>
 * Last note sticking when aborting the application while the sequence plays.<br>
 * I have yet to find a way to elegantly shutdown all running threads when aborting the console application.<br>
 * The best way right now is to let the application go thru its
 * full cycle (2 loops) until it quits by itself<br>- Any suggestions welcome! -
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

    // Sequencer engine
    public class SequencerEngine extends TimerTask {

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

            // Note duration has expired send a key up message (note OFF) to driver.
            playMidiNote(0);

            // Set new note and new duration
            note = SEQUENCE[seqStep][0];
            long duration = (int)((float)60 * 4 / SEQUENCE[seqStep][1] / TEMPO * 1000);

            // Loop amount reached -> quit the application.
            if (loopIndex == loopAmount) {
                t.cancel();
                doQuit();
                return;
            }

            // Get some random velocity with offset and send new note to driver.
            Random random = new Random();
            playMidiNote(VELOCITY + random.nextInt(127 - VELOCITY));

            // Increment sequence to next step
            if (++seqStep > SEQUENCE.length - 1) {
                loopIndex ++;
                seqStep = 0;
            }

            // Reschedule timer with next note duration;
            t.schedule(new SequencerEngine(), duration);
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

            // The number of time our sequence will repeat before quitting.
            loopAmount = 2;

            // Start playing the sequence within 1 second.
            t.schedule(new SequencerEngine(), 1000);

            // Keep our application going.
            keepRunning();

        } catch (MidiException me) {
            logger.error(String.valueOf(me));
        }
    }
}
