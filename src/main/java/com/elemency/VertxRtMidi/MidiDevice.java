package com.elemency.VertxRtMidi;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * Wraps an RtMidi object for C function return statuses.<br>
 * <i>native declaration : /run/media/elemency/Data/Prjs/SandBox/Midi/RtMidi/rtmidi_c.h:40</i><br>
 */
public class MidiDevice extends Structure {
    /**
     * The wrapped RtMidi object. (C type : void*)
     */
    public Pointer api;
    /**
     * C type : void*
     */
    public Pointer data;

    /**
     * True when the last function call was OK.
     */
    public byte ok;

    /**
     * If an error occured (ok != true), set to an error message. (C type : const char*)
     */
    public Pointer errorMsg;

    public MidiDevice() {
        super();
    }

    /**
     * @param api      C type : void*<br>
     * @param data     C type : void*<br>
     * @param errorMsg C type : const char*
     */
    public MidiDevice(Pointer api, Pointer data, byte ok, Pointer errorMsg) {
        super();
        this.api = api;
        this.data = data;
        this.ok = ok;
        this.errorMsg = errorMsg;
    }

    public MidiDevice(Pointer peer) {
        super(peer);
    }

    protected List<?> getFieldOrder() {
        return Arrays.asList("api", "data", "ok", "errorMsg");
    }

    public static class ByReference extends MidiDevice implements Structure.ByReference {

    }

    public static class ByValue extends MidiDevice implements Structure.ByValue {

    }
}
