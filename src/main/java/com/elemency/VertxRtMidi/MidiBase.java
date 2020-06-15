package com.elemency.VertxRtMidi;

import com.elemency.VertxRtMidi.RtMidiLib.RtMidiLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.IntBuffer;

public abstract class MidiBase {
    protected final RtMidiLibrary lib = RtMidiLibrary.INSTANCE;
    private final Logger logger = LoggerFactory.getLogger(MidiBase.class);
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
    public int getCompiledApi(IntBuffer apis, int apis_size) {
            return lib.rtmidi_get_compiled_api(apis, apis_size);
    }

    /**
     *
     */
    public String apiName(int api) {
            return lib.rtmidi_api_name(api);
    }

    /**
     *
     */
    public String apiDisplayName(int api) {
            return lib.rtmidi_api_display_name(api);
    }

    /**
     *
     */
    public int compiledApiByName(String name) {
            return lib.rtmidi_compiled_api_by_name(name);
    }

    /**
     *
     */
    public void error(int type, String errorString)  {
            lib.rtmidi_error(type, errorString);
    }

/* *********************************************************************************************************************
 * 											           MidiDevice Port API
 **********************************************************************************************************************/

    /**
     *
     */
    public void openPort(/*MidiDevice device, */int portNumber, String portName) throws Exception {
        try {
            lib.rtmidi_open_port(midiDevice, portNumber, portName);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public void openVirtualPort(/*MidiDevice midiDevice, */String portName) throws Exception {
        try {
            lib.rtmidi_open_virtual_port(midiDevice, portName);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public void closePort(/*MidiDevice device*/) throws Exception {
        try {
            logger.info("Closing Port...");
            lib.rtmidi_close_port(midiDevice);
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
    public String getPortName(/*MidiDevice device, */int portNumber) throws Exception {
        try {
            return lib.rtmidi_get_port_name(midiDevice, portNumber);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }
}
