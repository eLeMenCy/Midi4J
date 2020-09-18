package com.elemency.Midi4J.RtMidiDriver;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

/**
 * Wraps an RtMidi object for C function return statuses.<br>
 * <i>native declaration : RtMidi/rtmidi_c.h:40</i><br>
 */
public class RtMidiDevice extends Structure {

    /**
     * The wrapped RtMidi object. (C type : void*)
     */
    public Pointer instance;
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

    public RtMidiDevice() {
        super();
    }

    /**
     *
     * @param instance  C type : void*<br>
     * @param data      C type : void*<br>
     * @param ok        C type : byte <br>
     * @param errorMsg  C type : const char*
     */
    public RtMidiDevice(Pointer instance, Pointer data, byte ok, Pointer errorMsg) {
        super();
        this.instance = instance;
        this.data = data;
        this.ok = ok;
        this.errorMsg = errorMsg;
    }

    public RtMidiDevice(Pointer peer) {
        super(peer);

    }

    protected List<String> getFieldOrder() {
        return Arrays.asList("instance", "data", "ok", "errorMsg");
    }

    public static class ByReference extends RtMidiDevice implements Structure.ByReference {

    }

    public static class ByValue extends RtMidiDevice implements Structure.ByValue {

    }
}
