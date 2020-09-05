package com.elemency.Midi4J.examples.App2;

import com.elemency.Midi4J.MidiMessage;
import com.elemency.Midi4J.MidiOut;
import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class App2 extends Thread {
    private final Logger logger = LoggerFactory.getLogger(App2.class);

    private volatile boolean alive = true;
    private MidiOut midi4jOut = null;


    public void terminate() {
        this.alive = false;
    }

    @Override
    public void run() {
        while (alive) {
            int note = 60;
            int velocity = 100;

            try {
                MidiMessage tmp = MidiMessage.noteOn(1, note, velocity, 0);
                midi4jOut.sendMessage(tmp);
//            logger.info(tmp.getDescription());

                Thread.sleep(1000);

                tmp.setVelocity(0);
                midi4jOut.sendMessage(tmp);
//            logger.info(tmp.getDescription());

                Thread.sleep(500);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        midi4jOut.close();
    }

//    public static void main(String[] args) {
    public void init() {
        Scanner scan = new Scanner(System.in);
        String input = "";

        App2 t = new App2();

        t.midi4jOut = new MidiOut(RtMidi.Api.UNIX_JACK.getIntValue(), "Midi4J");
        t.midi4jOut.connect("OUT", 1, true);

        System.out.println("Input string: ");
        t.start();
        input = scan.nextLine();

        System.out.println("Your input string was: " + input);

        if (input.equals("Exit")) {
            System.out.println("terminate...");
            t.terminate();
        }
    }
}
