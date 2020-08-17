package com.elemency.Midi4J;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MidiException extends RuntimeException {
    private final Logger logger = LoggerFactory.getLogger(MidiException.class);

    /**
     * <code>MidiException</code> instance created without detailed message.
     */
    public MidiException() {
    }


    /**
     * <code>MidiException</code> instance created with local detailed message.
     *
     * @param msg detailed message.
     */
    public MidiException(String msg) {
        super(msg);
    }

    /**
     * <code>MidiException</code> instance created with link to current cause.
     *
     * @param cause exception current cause.
     */
    public MidiException(Throwable cause) {
        super(cause);
    }

    /**
     * <code>MidiException</code> instance created with detailed message and link to current cause.
     *
     * @param msg   detailed message.
     * @param cause exception current cause.
     */
    public MidiException(String msg, Throwable cause) {
        super(msg, cause);
    }

//    /***************************************************************************
//     * <code>MidiException</code> instance created without detailed message.
//     */
//    public String getMessage(RtMidiDevice device) {
//        byte[] buffer = device.errorMsg.getByteArray(0,128);
//        buffer = Arrays.copyOfRange(buffer, 16, 128);
//        String msg = new String(buffer, StandardCharsets.UTF_8);
//
//        return msg.substring(0, msg.indexOf('\0'));
//    }
//
//    /**
//     * <code>MidiException</code> instance created with detailed native message.
//     */
//    public MidiException(RtMidiDevice device) {
//        logger.info(getMessage(device));
//    }


}
