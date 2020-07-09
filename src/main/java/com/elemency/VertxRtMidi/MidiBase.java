package com.elemency.VertxRtMidi;

import com.elemency.VertxRtMidi.RtMidiDriver.RtMidi;
import com.elemency.VertxRtMidi.RtMidiDriver.RtMidiLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public abstract class MidiBase implements AutoCloseable {
    protected final RtMidiLibrary lib = RtMidiLibrary.INSTANCE;
    private final Logger logger = LoggerFactory.getLogger(MidiBase.class);
    protected MidiDevice midiDevice = null;
    protected String clientName = "Midi4J";

/* *********************************************************************************************************************
 * 											           MidiDevice
 **********************************************************************************************************************/

    /**
     *
     */
    public void free() {
        try {
            if (getDeviceClassName().compareTo("MidiIn") == 0) {
                lib.rtmidi_in_free(midiDevice);

            } else {
                lib.rtmidi_out_free(midiDevice);
            }
            if (midiDevice.ok == 0) throw new MidiException();
            logger.info(getDeviceClassName() + " memory ... freed");
        } catch (Throwable throwable) {
        }
    }

    /**
     *
     */
    private String getDeviceClassName() {
        String deviceClassName = this.getClass().getTypeName();
        return deviceClassName.substring(deviceClassName.lastIndexOf(".") + 1);
    }

    /**
     *
     */
    private String getTargetDeviceType() {
        return getDeviceClassName().equals("MidiIn") ? "Out" : "In";
    }

    /**
     *
     */
    public void error(int type, String errorString) {
        lib.rtmidi_error(type, errorString);
    }

    /**
     *
     */
    public int getCurrentApiId() {
        return lib.rtmidi_out_get_current_api(midiDevice);
    }

    /**
     *
     */
    public String getCurrentApiName() {
        return new RtMidi().apiDisplayName(getCurrentApiId());
    }

/* *********************************************************************************************************************
 * 											           MidiDevice Port
 **********************************************************************************************************************/

    /**
     *
     */
    public boolean isPortOpen() {
        return lib.rtmidi_is_port_open(this.midiDevice);
    }

    /**
     *
     */
    public void displayErrorFromNative() {
        String msgRaw = "";
        String msg = "";

        byte[] bbuf = this.midiDevice.errorMsg.getByteArray(0, 128);
        msgRaw = new String(bbuf, StandardCharsets.UTF_8);
        System.out.println("errorMsg raw: " + msgRaw);

        bbuf = Arrays.copyOfRange(bbuf, 16, 128);
        msg = new String(bbuf, StandardCharsets.UTF_8);
        msg = msg.substring(0, msg.indexOf('\0'));

        System.out.println("errorMsg cleaned: " + msg + "\nlength: " + msg.length());
    }

    /**
     *
     */
    public boolean openPort(String fromPortName, int toPortId, boolean autoConnect) {

        System.out.println("");
        logger.info("Trying to connect " + getClientName(toPortId) + "'s " + getTargetDeviceType() + " port (id " + toPortId + ") to " +
                clientName + "'s " + fromPortName + " port...");

        if (toPortId < 0 || toPortId > getPortCount() - 1) {
            logger.warn(getTargetDeviceType() + " port (id " + toPortId + ") doesn't exist, " +
                    "please select another one or check that the " + getCurrentApiName() + " Midi API is active!");
            return false;
        }

        lib.rtmidi_open_port(this.midiDevice, toPortId, fromPortName, autoConnect);

        if (this.midiDevice.ok != 0) {
            String msg3 = clientName + "'s " + fromPortName +
                    " port and " + getClientName(toPortId) + "'s " + getTargetDeviceType() +
                    " port (id " + toPortId + ") have been opened succesfully" +
                    (autoConnect ? " and, at your request, connected together!" : " but, at your request, were left disconnected!");

            logger.info(msg3);

            return true;
        }
        else {
            showNativeMsg();
        }

        return false;
    }


    /**
     *
     */
    public boolean openVirtualPort(String portName) {
        if (midiDevice.ok != 0) {
            lib.rtmidi_open_virtual_port(midiDevice, portName);
        } else {
            System.out.println("Virtual Port not connected");
        }
        return midiDevice.ok != 0;
    }

    /**
     *
     */
    public boolean closePort() {
        try {
            lib.rtmidi_close_port(midiDevice);
            if (midiDevice.ok == 0) throw new MidiException();
            logger.info(getDeviceClassName() + " port ... closed");

        } catch (Throwable e) {
            if (midiDevice.ok != 0) {
                System.out.println("Device not found - unable to close its port.");
            }
        }
        return midiDevice.ok != 0;
    }

    /**
     *
     */
    public int getPortCount() {
        int ports = lib.rtmidi_get_port_count(midiDevice);
        return ports;
    }

    /**
     *
     */
    public String getFullPortName(int portNumber) {
        String deviceName = "Unknown";
//        if (midiDevice.ok != 0) {
        deviceName = lib.rtmidi_get_port_name(this.midiDevice, portNumber);
//        }
//        else {
//            System.out.println("Device not found - unable to provide its name.");
//        }
        return deviceName;
    }

    /**
     *
     */
    public String getClientName(int portNumber) {
        String fullPortName = getFullPortName(portNumber);

        if (fullPortName.equals("")) return "Unknown";

        int stop = fullPortName.indexOf(":");
        return fullPortName.substring(0, stop);
    }

    /**
     *
     */
    public String getPortName(int portNumber) {
        String fullPortName = getFullPortName(portNumber);

        if (fullPortName.equals("")) return "Unknown";

        int start = fullPortName.indexOf(":") + 1;
        int stop = fullPortName.lastIndexOf(" ");

        return fullPortName.substring(start, stop);
    }

    /**
     *
     */
    public void setPortName(String portName) {
        // TODO: throw exception
        lib.rtmidi_set_port_name(this.midiDevice, portName);
    }

    /**
     *
     */
    public void setClientName(String clientName) {
        // TODO: exception.
        lib.rtmidi_set_client_name(this.midiDevice, clientName);
    }

    /**
     *
     */
    public void listConnectablePorts() {
        int ports = getPortCount();

        System.out.println("");
        logger.info("There " + (ports > 1 ? "are " : "is ") +
                (ports == 0 ? "no" : ports) + " " + getCurrentApiName() + " Midi " +
                getTargetDeviceType() + " port" + (ports > 1 ? "s" : "") + " available." +
                (ports == 0 ? " Is " + getCurrentApiName()  + " running?" : ""));

        if (ports < 1) return;

        for (int i = 0; i < ports; i++) {
            logger.info(getTargetDeviceType() + " port id(" + i + ") full name: " + getFullPortName(i));
        }
    }

    protected void showNativeMsg() {
        String msg;
        byte[] bbuf = midiDevice.errorMsg.getByteArray(0,128);
        String msgRaw = new String(bbuf, StandardCharsets.UTF_8);
        System.out.println("errorMsg raw: " + msgRaw);

        bbuf = Arrays.copyOfRange(bbuf, 16, 128);
        msg = new String(bbuf, StandardCharsets.UTF_8);
        msg = msg.substring(0, msg.indexOf('\0'));

        System.out.println("errorMsg cleaned: " + msg + "\nlength: " + msg.length());
    }

}
