package com.elemency.VertxRtMidi;

import com.elemency.VertxRtMidi.RtMidiLib.RtMidiLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.nio.IntBuffer;

public abstract class MidiBase implements AutoCloseable {
    protected final RtMidiLibrary lib = RtMidiLibrary.INSTANCE;
    private final Logger logger = LoggerFactory.getLogger(MidiBase.class);
    protected MidiDevice midiDevice = null;

/* *********************************************************************************************************************
 * 											           MidiDevice API
 **********************************************************************************************************************/

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
    public void free() throws Exception {
        if (getDeviceType().compareTo("MidiIn") == 0) {
            lib.rtmidi_in_free(midiDevice);
        } else {
            lib.rtmidi_out_free(midiDevice);
        }
        logger.info(getDeviceType() + " memory ... freed");
    }

    /**
     *
     */
    private String getDeviceType() {
        String deviceType = this.getClass().getTypeName();
        return deviceType.substring(deviceType.lastIndexOf(".") + 1);
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
    public void openPort(String fromPortName, int toPortNumber) throws Exception {
        try {
            lib.rtmidi_open_port(midiDevice, toPortNumber, fromPortName);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public void openVirtualPort(String portName) throws Exception {
        try {
            lib.rtmidi_open_virtual_port(midiDevice, portName);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
    public void closePort() throws Exception {
        try {
            lib.rtmidi_close_port(midiDevice);
            logger.info(getDeviceType() + " port ... closed");
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    /**
     *
     */
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
    public String getPortName(int portNumber) throws Exception {
        try {
            return lib.rtmidi_get_port_name(midiDevice, portNumber);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }
}
