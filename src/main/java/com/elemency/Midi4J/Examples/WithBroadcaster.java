package com.elemency.Midi4J.Examples;

import com.elemency.Midi4J.*;
import com.elemency.Midi4J.Broadcaster.BroadcastListener;
import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This sample illustrates how to use the Broadcaster/listener system to handle
 * all messages coming from our 2 native MidiIn source devices and reroute them
 * to the relevant connected target device.
 */
public class WithBroadcaster extends KeepRunning implements BroadcastListener, AppOption {
    private final String sampleTitle = "Method with Broadcaster";
    private final Logger logger = LoggerFactory.getLogger(WithBroadcaster.class);
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

            // Message Routing.
            if (midi4jIn.getSourceDeviceUUID().equals(uuid)) {
                midi4jOut.sendMessage(midiMessage);

            } else if (midi4j2In.getSourceDeviceUUID().equals(uuid)) {
                midi4j2Out.sendMessage(midiMessage);
            }

            logger.info(
                    SmpteTimecode.getTimecode(SmpteTimecode.getElapsedTimeSinceStartTime()) +
                            midiMessage.timeStampAsTimecode() + midiMessage.getDescription()
            );

        } catch (MidiException | NullPointerException | IllegalStateException me) {
                me.printStackTrace();
        }
    }

    @Override
    public void init() throws Exception {

        System.out.println("---------------------------");
        System.out.println("| " + sampleTitle + " |");
        System.out.println("---------------------------\n");

        try (
                MidiOut midi4jOut = new MidiOut(RtMidi.Api.UNIX_JACK.getIntValue(), "Midi4J");
                MidiIn midi4jIn = new MidiIn(RtMidi.Api.LINUX_ALSA.getIntValue(), "Midi4J", 100, false);
                MidiOut midi4j2Out = new MidiOut(RtMidi.Api.UNIX_JACK.getIntValue(), "Midi4J2");
                MidiIn midi4j2In = new MidiIn(RtMidi.Api.LINUX_ALSA.getIntValue(), "Midi4J2", 100, false);
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
                outDevices = this.midi4jIn.listTargetDevices(false);
                this.midi4jIn.connect("IN", 2, true);
                this.midi4j2In.connect("IN", 2, true);

                // Register our listener app to the Broadcaster.
                Broadcaster.register(this);

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
