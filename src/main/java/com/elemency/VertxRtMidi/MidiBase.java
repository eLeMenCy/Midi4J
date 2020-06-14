package com.elemency.VertxRtMidi;

import com.elemency.VertxRtMidi.RtMidiLib.RtMidiLibrary;

import java.nio.IntBuffer;

public abstract class MidiBase {
    private final RtMidiLibrary lib = RtMidiLibrary.INSTANCE;
    protected MidiDevice midiDevice = null;


/* *********************************************************************************************************************
 * 											           MidiDevice API
 **********************************************************************************************************************/
    /**
     *
     */
    abstract public MidiDevice getMidiDevice();

    /**
     *
     */
    public int getCompiledApi(IntBuffer apis, int apis_size) throws Exception {
        try {
            return lib.rtmidi_get_compiled_api(apis, apis_size);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public String apiName(int api) throws Exception {
        try {
            return lib.rtmidi_api_name(api);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public String apiDisplayName(int api) throws Exception {
        try {
            return lib.rtmidi_api_display_name(api);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public int compiledApiByName(String name) throws Exception {
        try {
            return lib.rtmidi_compiled_api_by_name(name);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public void error(int type, String errorString) throws Exception {
        try {
            lib.rtmidi_error(type, errorString);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /* *********************************************************************************************************************
     * 											           MidiDevice Port API
     **********************************************************************************************************************/

    /**
     *
     */
    public void openPort(MidiDevice device, int portNumber, String portName) throws Exception {
        try {
            lib.rtmidi_open_port(device, portNumber, portName);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public void openVirtualPort(MidiDevice device, String portName) throws Exception {
        try {
            lib.rtmidi_open_virtual_port(device, portName);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public void closePort(MidiDevice device) throws Exception {
        try {
            lib.rtmidi_close_port(device);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
//    public int getPortCount(MidiDevice device) throws Exception {
    public int getPortCount() throws Exception {
        try {
            return lib.rtmidi_get_port_count(midiDevice);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public String getPortName(MidiDevice device, int portNumber) throws Exception {
        try {
            return lib.rtmidi_get_port_name(device, portNumber);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }
}
