package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidiDevice;
import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.ochafik.lang.jnaerator.runtime.NativeSizeByReference;
import com.sun.jna.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class MidiIn extends MidiBase {
    protected final Logger logger = LoggerFactory.getLogger(MidiIn.class);
//    private final RtMidiLibrary lib = super.lib;

    public MidiIn() throws MidiException {
        int api = RtMidi.Api.UNSPECIFIED.getIntValue();
        super.rtMidiDevice = create(api, super.deviceName, 100);
    }

    public MidiIn(int api, String deviceName, int queueSizeLimit) throws MidiException {
        if (!deviceName.isEmpty()) {

            // Remove the eventual semicolon form client name.
            // The semicolon is generally used as a separator between client and port name and id).
            deviceName = deviceName.replaceAll(":"," ");
            super.deviceName = deviceName;
        }
        super.rtMidiDevice = create(api, super.deviceName, queueSizeLimit);

//        TODO: setCallback(process, "native", null);
//         Set the native callback in this class
//         calling back a user sub callback (java)
//         passing the midi data encapsulated in a MidiMessage.
//         See if there are better ways in doing so as to minimise latency.
//         (see commented lambda function at the end of this code).
    }

    @Override
    public void close()/* throws MidiException*/ {
        cancelCallback();
        closeDevice();
        free();
    }

    /**
     *
     *
     */
    @Override
    public void free() {
        try {
            lib.rtmidi_in_free(rtMidiDevice);
            if (rtMidiDevice.ok == 0) throw new MidiException();
            logger.info(getDeviceClassName() + " memory ... freed");
        } catch (Throwable throwable) {

        }
    }

    /**
     *
     */
    @Override
    public int getCurrentApiId() {
        return lib.rtmidi_in_get_current_api(rtMidiDevice);
    }

    /**
     *
     */
    private RtMidiDevice create(int api, String clientName, int queueSizeLimit) throws MidiException {

        RtMidiDevice midiDevice = lib.rtmidi_in_create(api, clientName, queueSizeLimit);
        return midiDevice;
    }

    /**
     *
     */
    public boolean setCallback(FromNative callback, String threadName, Pointer userData) /*throws Exception*/ {

        /**
         * The CallbackThreadInitializer ensures that the VM doesn't generate multiple Java Threads for the same
         * native thread. This must be done before attaching the thread to the VM.
         */
        CallbackThreadInitializer cti = new CallbackThreadInitializer(false, false, threadName);
        Native.setCallbackThreadInitializer(callback, cti);

        lib.rtmidi_in_set_callback(rtMidiDevice, callback, userData);
//        if (midiDevice.ok == 0) throw new MidiException(midiDevice);
        return rtMidiDevice.ok != 0;
    }

    /**
     *
     */
    public boolean cancelCallback() {
        logger.info("Cancelling IN callback...");
        lib.rtmidi_in_cancel_callback(rtMidiDevice);
//        if (midiDevice.ok == 0) throw new MidiException(midiDevice);
        return rtMidiDevice.ok != 0;
    }

    /**
     *
     */
    public void ignoreTypes(byte midiSysex, byte midiTime, byte midiSense) {
        lib.rtmidi_in_ignore_types(rtMidiDevice, midiSysex, midiTime, midiSense);
    }

    /**
     *
     */
    public double getMessage(ByteBuffer message, NativeSizeByReference size) {
        double midiMessage = lib.rtmidi_in_get_message(rtMidiDevice, message, size);
//        if (midiDevice.ok == 0) throw new MidiException(midiDevice);
        return midiMessage;
    }

    /**
     * RtMidiIn callback function.
     * See ref RtMidiIn::RtMidiCallback.
     * <i>native declaration : /run/media/elemency/Data/Prjs/SandBox/Midi/RtMidi/rtmidi_c.h</i>
     */
    public interface FromNative extends Callback {

        /**
         * @param timeStamp   The time at which the message has been received.
         * @param message     The midi message.
         * @param messageSize Size of the Midi message.
         * @param userData    Additional user data for the callback.
         */
        void process(double timeStamp, Pointer message, NativeSize messageSize, Pointer userData);
    }

//    /**
//     * Midi In data from native.
//     *
//     * @param timeStamp     The time at which the message has been received.
//     * @param message       The midi message.
//     * @param messageSize   Size of the Midi message.
//     * @param userData      Additional user data.
//     */
//    private final MidiInFromNative process = (timeStamp, message, messageSize, userData) -> {
//        // Byte array to receive the event from native pointer.
//        byte[] data = new byte[messageSize.intValue()];
////        if (!doQuit) {
//            // Read native memory data into our data byte array.
//            message.read(0, data, 0, messageSize.intValue());
//
//            //data -> Byte 0 = 144, Byte 1 = 77, Byte 2 = 0, stamp = 0.107015
//
//            if ((data[0] & 0xFF) == 144 && data[1] == 39) {
//                logger.info("quitting...");
////                doQuit();
//                return;
//            }
//
////            midi4jOut.sendMessage(data, messageSize.intValue());
//
//            String log = "";
//            for (int i = 0; i < messageSize.intValue(); i++) {
//                if (i == 0) {
//                    int status = data[i] & 0xFF;
//                    log += "Byte 0 = 0x" + Integer.toHexString(status) + "(" + status + "), ";
//                } else {
//                    log += "Byte " + i + " = " + data[i] + ", ";
//                }
//            }
//            log += "Stamp = " + String.format("%1.10s", timeStamp);
//
//            logger.debug(log);
////        }
//    };

}
