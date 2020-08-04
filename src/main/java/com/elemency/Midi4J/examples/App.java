/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.elemency.Midi4J.examples;

import com.elemency.Midi4J.*;
import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.valueOf;

public class App extends KeepAppRunning {
    private final Logger logger = LoggerFactory.getLogger(App.class);
    private MidiIn midi4jIn = null;
    private MidiOut midi4jOut = null;




    public void processMidiInMessage(double timeStamp, MidiMessage midiMessage, Pointer userData) {
        if (!doQuit) {
            if (midiMessage.isNoteOn(false) && midiMessage.getNoteNumber() == 39) {
                logger.info("quitting...");
                doQuit();
                return;
            }
        }

        midi4jOut.sendMessage(midiMessage);

//        System.out.println("getChannel: " + midiMessage.getChannel());
//        System.out.println("isForChannel(1): " + midiMessage.isForChannel(1));
//        System.out.println("isNoteOn: " + midiMessage.isNoteOn(false));
//        System.out.println("isNoteOff: " + midiMessage.isNoteOff(true));
//        System.out.println("isNoteOnOrOff: " + midiMessage.isNoteOnOrOff());
//        System.out.println("getNoteNumber: " + midiMessage.getNoteNumber());
//        System.out.println("getVelocity: " + midiMessage.getVelocity());
//        System.out.println("getFloatVelocity: " + midiMessage.getFloatVelocity());
//        System.out.println("isChannelAftertouch: " + midiMessage.isChannelAftertouch());
//        System.out.println("getChannelAftertouchValue: " + midiMessage.getChannelAftertouchValue());
//        System.out.println("isPolyAftertouch: " + midiMessage.isPolyAftertouch());
//        System.out.println("getPolyAftertouchValue: " + midiMessage.getPolyAftertouchValue());
//        System.out.println("getMidiNoteName: " + midiMessage.getMidiNoteName(midiMessage.getNoteNumber(), true, true, 3));
//        System.out.println("getDescription: " + midiMessage.getDescription());
        logger.info(midiMessage.timeStampToTimecode() + midiMessage.getDescription());

//        midi4jOut.sendMessage(MidiMessage.noteOn(1, 60, 64));
//        midi4jOut.sendMessage(MidiMessage.noteOn(1, 60, 0));

    }

    public static void main(String[] args) throws Exception {
            App midiInApp = new App();
            midiInApp.Init();
    }

    @Override
    protected void Init() throws Exception {

        try (
                MidiOut midi4jOut = new MidiOut(RtMidi.Api.UNIX_JACK.getIntValue(), "Midi4J");
                MidiIn midi4jIn = new MidiIn(RtMidi.Api.LINUX_ALSA.getIntValue(), "Midi4J", 100, this)
//                MidiOut midi4jOut = new MidiOut();
//                MidiIn midi4jIn = new MidiIn();
        ) {

            this.midi4jIn = midi4jIn;
            this.midi4jOut = midi4jOut;

            System.out.println("Out device count: "+ this.midi4jIn.getDeviceCount());
            System.out.println("In device count: "+ this.midi4jOut.getDeviceCount());

            this.midi4jIn.listTargetDevices();
            this.midi4jIn.connect("IN", 2, true);

            this.midi4jOut.listTargetDevices();
            this.midi4jOut.connect("OUT", 1, true);

            System.out.println("\nThis Midi In Device name is: " + this.midi4jIn.getDeviceName());
            System.out.println("A possible target Device name is: " + this.midi4jIn.getTargetDeviceName(5));
            System.out.println("and its Port name is: " + this.midi4jIn.getTargetPortName(5));

            System.out.println("\nName of Out port id(" + 1 + ") is: " + this.midi4jIn.getTargetPortName(1));
            System.out.println("Name of In port id(" + 0 + ") is: " +  this.midi4jOut.getTargetPortName(0));

            System.out.println("\nis midi4jIn device Open: " + midi4jIn.isDeviceOpen());
            System.out.println("is midi4jOut device Open: " + midi4jOut.isDeviceOpen());

            System.out.println("--------------------------------------------");

            this.midi4jIn.listTargetDevices();
            this.midi4jOut.listTargetDevices();

            keepRunning();

        } catch( MidiException me) {
            logger.error(String.valueOf(me));

//        } catch( Exception e) {
//            logger.error("An unrecoverable error occurred:\n e\n - quitting...");
        }
    }
}
