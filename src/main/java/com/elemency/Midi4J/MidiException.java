package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidiDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MidiException extends  Exception {
    private final Logger logger = LoggerFactory.getLogger(MidiException.class);

    /**
     * <code>MidiException</code> instance created without detailed message.
     */
    public String getMessage(RtMidiDevice device) {
        byte[] buffer = device.errorMsg.getByteArray(0,128);
        buffer = Arrays.copyOfRange(buffer, 16, 128);
        String msg = new String(buffer, StandardCharsets.UTF_8);

        return msg.substring(0, msg.indexOf('\0'));
    }

    /**
     * <code>MidiException</code> instance created without detailed message.
     */
    public MidiException() {
    }

    /**
     * <code>MidiException</code> instance created with detailed native message.
     */
    public MidiException(RtMidiDevice device) {
        logger.info(getMessage(device));
    }

    /**
     * <code>MidiException</code> instance created with local detailed message.
     * @param msg detailed message.
     */
    public MidiException(String msg) {
        super(msg);
    }

    /**
     * <code>MidiException</code> instance created with link to current cause.
     * @param e exception current cause.
     */
    public MidiException(Throwable e) {
        super(e);
    }

    /**
     * <code>MidiException</code> instance created with detailed message and link to current cause.
     * @param msg detailed message.
     * @param e exception current cause.
     */
    public MidiException(String msg, Throwable e) {
        super(msg, e);
    }
}
