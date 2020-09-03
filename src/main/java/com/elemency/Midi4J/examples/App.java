package com.elemency.Midi4J.examples;

import com.elemency.Midi4J.*;
import com.elemency.Midi4J.Broadcaster.BroadcastListener;
import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class App extends KeepAppRunning implements BroadcastListener {
    private final Logger logger = LoggerFactory.getLogger(App.class);
    private MidiIn midi4jIn = null;
    private MidiOut midi4jOut = null;

    public static void main(String[] args) throws Exception {
        App midiInApp = new App();
        midiInApp.Init();
    }

    @Override
    public void receiveMessage(UUID uuid, MidiMessage midiMessage, Pointer userData) {
        try {
            if (!doQuit) {
                if (midiMessage.getControllerNumber() == 89 && midiMessage.getControllerValue() == 127) {
                    logger.info("quitting...");
                    Broadcaster.unregister(this);
                    doQuit();
                    return;
                }
            }

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

    @Override
    protected void Init() throws Exception {

        try (
                MidiOut midi4jOut = new MidiOut(RtMidi.Api.UNIX_JACK.getIntValue(), "Midi4J");
                MidiIn midi4jIn = new MidiIn(RtMidi.Api.LINUX_ALSA.getIntValue(), "Midi4J", 100, false);
        ) {
            this.midi4jIn = midi4jIn;
            this.midi4jOut = midi4jOut;

            Broadcaster.register(this);

            try {
                System.out.println("Target Out device count: " + this.midi4jIn.getTargetDeviceCount());
                System.out.println("Target In device count: " + this.midi4jOut.getTargetDeviceCount());

                // List all available target Alsa OUT devices.
                List<Map<String, String>> outDevices = this.midi4jIn.listTargetDevices(false);
                this.midi4jIn.connect("IN", 2, true);

                // List all available target Jack In devices.
                List<Map<String, String>> inDevices = this.midi4jOut.listTargetDevices(false);
                for (int i = 0; i < 3; i++) {
                    this.midi4jOut.connect("OUT", i, true);
                }

                System.out.println("Out device count: " + this.midi4jIn.getTargetDeviceCount());
                System.out.println("In device count: " + this.midi4jOut.getTargetDeviceCount());

                System.out.println("\nThis Midi In Device name is: " + this.midi4jIn.getSourceDeviceName());
                System.out.println("A possible target Device name is: " + this.midi4jIn.getTargetDeviceName(5));
                System.out.println("and its Port name is: " + this.midi4jIn.getTargetPortName(5));

                System.out.println("\nName of Out port id(" + 1 + ") is: " + this.midi4jIn.getTargetPortName(1));
                System.out.println("Name of In port id(" + 0 + ") is: " + this.midi4jOut.getTargetPortName(0));

                System.out.println("\nis midi4jIn device Open: " + midi4jIn.isSourceDeviceOpen());
                System.out.println("is midi4jOut device Open: " + midi4jOut.isSourceDeviceOpen());

                System.out.println("--------------------------------------------");

                outDevices = this.midi4jIn.listTargetDevices(false);
                inDevices = this.midi4jOut.listTargetDevices(false);

//                System.out.println("------: " + this.midi4jIn.getTargetDeviceName(85));
                midi4jIn.setSourceDeviceName("tt");
                System.out.println(midi4jIn.getSourceDeviceName());
                midi4jIn.setSourcePortName("Huuuuh");
                System.out.println(midi4jIn.getSourcePortName());
//                System.out.println(midi4jIn.getTargetPortName(-2));

                MidiMessage m1 = new MidiMessage(MidiMessage.createStatusByte(0x78, 1), 0);
//                logger.info(m1.getDescription());

                // Short 2 byte message test
                MidiMessage m2 = new MidiMessage(MidiMessage.createStatusByte(0x73, 1), (60 & 127), 0);
                logger.info(m2.getDescription());

                // Short 3 byte message test
                MidiMessage m3 = new MidiMessage(MidiMessage.createStatusByte(0x80, 1), (60 & 127), 80, 0);
                logger.info(m3.getDescription());

                // Short message test via MidiMessage::byte[] constructor.
                byte[] tmp = {(byte) 0x91, 75, 81, 78};
                MidiMessage m4 = new MidiMessage(tmp, tmp.length, 0);
                logger.info(m4.getDescription());

                // SysEx message test using MidiMessage constructor.
                byte[] tmp2 = {(byte) 0xF0, 60, 81, 123, 5, (byte) 0xF7};
                MidiMessage m5 = new MidiMessage(tmp2, tmp2.length, 0);
                logger.info(m5.getDescription());

                // SysEx message test using createSysExMessage().
                byte[] tmp3 = {(byte) 0xF0, 61, 82, 124, 6, (byte) 0xF7};
                m5 = MidiMessage.createSysExMessage(tmp3, tmp3.length, 0);
                logger.info(m5.getDescription());

                // Replace midimessage timeStamp
                MidiMessage m6 = new MidiMessage(MidiMessage.createStatusByte(0x90, 1), (60 & 127), (85 & 127), 1000);
                m6 = m6.withTimeStamp(6);
                logger.info(m6.getDescription() + " timeStamp: " + m6.getTimeStamp());

//                try {
                // Native message Test
                Pointer test = null;
                NativeSize testSize = new NativeSize(0);
                MidiMessage tc = new MidiMessage(test, testSize, 0);
                logger.info(tc.getDescription());
//                } catch(AppException e) {
//
//                    System.out.println("AppException::notifyUser(lookupErrorText():" + e);
//                    System.out.println("AppException::notifyNonUsers(e):" + e);
//
//                } catch(Throwable t) {
//
//                    System.out.println("Throwable::notifyUser(lookupErrorText():" + t);
//                    System.out.println("Throwable::notifyNonUsers(e):" + t);
//                }

            } catch (MidiException | NullPointerException me) {
                me.printStackTrace();
            }

            t = new Timer();
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {

                    Random random = new Random();
                    int note = random.nextInt(127);
                    int velocity = random.nextInt(127);

                    MidiMessage tmp = MidiMessage.noteOn(1, note, velocity, 0);
                    midi4jOut.sendMessage(tmp);
                    logger.info(tmp.getDescription());

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    tmp.setFloatVelocity(0);
                    midi4jOut.sendMessage(tmp);
                    logger.info(tmp.getDescription());
                }
            };

//            t.schedule(tt,1000,250);

            keepRunning();

        } catch (MidiException me) {
            logger.error(String.valueOf(me));

//        } catch( Exception e) {
//            logger.error("An unrecoverable error occurred:\n e\n - quitting...");
        }
    }

}
