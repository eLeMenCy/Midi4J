package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidiDevice;
import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import com.elemency.Midi4J.RtMidiDriver.RtMidiLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.MidiEvent;
import java.util.ArrayList;


public abstract class MidiBase implements AutoCloseable {
    protected final RtMidiLibrary lib = RtMidiLibrary.INSTANCE;
    private final Logger logger = LoggerFactory.getLogger(MidiBase.class);
    protected RtMidiDevice rtMidiDevice = null;
    protected String clientName = "Midi4J";
    protected ArrayList<MidiDevice> midiDevices = new ArrayList<>();

/* *********************************************************************************************************************
 * 											           RtMidiDevice
 **********************************************************************************************************************/

    /**
     *
     *
     */
    public void free() {
        try {
            if (getDeviceClassName().compareTo("MidiIn") == 0) {
                lib.rtmidi_in_free(rtMidiDevice);

            } else {
                lib.rtmidi_out_free(rtMidiDevice);
            }
            if (rtMidiDevice.ok == 0) throw new MidiException();
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
        return lib.rtmidi_out_get_current_api(rtMidiDevice);
    }

    /**
     *
     */
    public String getCurrentApiName() {
        return new RtMidi().apiDisplayName(getCurrentApiId());
    }

    /**
     *
     */
    public void setClientName(String clientName) {
        // TODO: exception.
        try {
            if (clientName.isEmpty()) {
                throw new MidiException("setClientName() -> Client name cannot be empty!");
            }

            lib.rtmidi_set_client_name(this.rtMidiDevice, clientName);
            this.clientName = clientName;

        }
        catch (MidiException msg) {
            logger.warn(msg.getMessage());
        }
    }

    /**
     *
     */
    public String getClientName(int portId) {
        String fullPortName = getFullDeviceDetails(portId);

        if (fullPortName.equals("")) return "Unknown";

        int stop = fullPortName.indexOf(":");
        return fullPortName.substring(0, stop);
    }

    /**
     *
     */
    public String getClientName() {
        return this.clientName;
    }

/* *********************************************************************************************************************
 * 											           RtMidiDevice Port
 **********************************************************************************************************************/

    /**
     *
     */
    public boolean isDeviceOpen() {
        return lib.rtmidi_is_port_open(this.rtMidiDevice);
    }

    /**
     *
     */
    public boolean connectDevices(String fromPortName, int toPortId, boolean autoConnect) {

        int deviceCount = getDeviceCount();
        boolean portIdIsValid = ((toPortId > -1) && (toPortId < deviceCount));

        System.out.println("");
        logger.info("Trying to " + (autoConnect ? "open and connect " : "open ") + "both " + getClientName(toPortId) + " " +
                getTargetDeviceType() + " port (id " + toPortId + ") and " +
                this.clientName + "'s " + fromPortName + " port...");

        if (!portIdIsValid) {
            logger.warn(getTargetDeviceType() + " port (id " + toPortId + ") doesn't exist - " +
                    "Are the " + getCurrentApiName() + " Midi API and/or your Midi sw/hw running?");
        }

        String word1 = Misc.getFirstWord(this.clientName);
        String word2 = Misc.getFirstWord(getClientName(toPortId));

        if (word1.equals(word2)) {
            autoConnect = false;
        }

        lib.rtmidi_open_port(this.rtMidiDevice, toPortId, fromPortName, autoConnect);

        if (this.rtMidiDevice.ok != 0) {
            String msg;

            if (portIdIsValid) {
                msg = this.clientName + "'s " + fromPortName +
                        " port " + (portIdIsValid ? "and " + getClientName(toPortId) + "'s " + getTargetDeviceType() +
                        " port (id " + toPortId + ") have been opened succesfully" +
                        (autoConnect ? " and, at your request, connected together!" : " but, at your request, were left disconnected!") : "");
            } else {
                msg = "Couldn't find " + getTargetDeviceType() + " port (id " + toPortId + ") so only " +
                        this.clientName + "'s " + fromPortName + " port could be opened.";
            }

            logger.info(msg);

            return true;
//        }
//        else {
//            System.out.println(getNativeMsg());
        }

        return false;
    }


    /**
     *
     */
    public boolean openVirtualDevice(String deviceName) {
        if (rtMidiDevice.ok != 0) {
            lib.rtmidi_open_virtual_port(rtMidiDevice, deviceName);
        } else {
            System.out.println("Virtual Port not connected");
        }
        return rtMidiDevice.ok != 0;
    }

    /**
     *
     */
    public int getDeviceCount() {
        int deviceCount = lib.rtmidi_get_port_count(rtMidiDevice);
        return deviceCount;
    }

    /**
     *
     */
    public String getFullDeviceDetails(int portId) {
        String fullDeviceDetails = "??";
//        if (midiDevice.ok != 0) {
        fullDeviceDetails = lib.rtmidi_get_port_name(this.rtMidiDevice, portId);
//        }
//        else {
//            System.out.println("Device not found - unable to provide its name.");
//        }
        return fullDeviceDetails;
    }

    /**
     *
     */
    public void setPortName(String portName) {
        // TODO: throw exception
        lib.rtmidi_set_port_name(this.rtMidiDevice, portName);
    }

    /**
     *
     */
    public String getPortName(int portId) {
        if (getDeviceCount() < 1) {
            return "No such a port";
        }

        String fullDeviceDetails = getFullDeviceDetails(portId);
        if (fullDeviceDetails.equals("")) return "??";

        int start = fullDeviceDetails.indexOf(":") + 1;
        int stop = fullDeviceDetails.lastIndexOf(" ");

        // Client and port are not referenced in device details open under the Jack API.
        if (stop < start) {
            stop = fullDeviceDetails.length();
        }

        return fullDeviceDetails.substring(start, stop);
    }

    /**
     *
     * @return
     */
    public ArrayList<MidiDevice> listDevices() {
        int deviceCount = getDeviceCount();
        MidiDevice midiDevice = null;

        System.out.println("");
        if (deviceCount < 1) {
            logger.warn("There are no " + getCurrentApiName() + " Midi " +
                    getTargetDeviceType() + " ports" + (deviceCount > 1 ? "s" : "") + " available." +
                    (deviceCount == 0 ? " Are the " + getCurrentApiName()  + " API and/or your Midi sw/hw running?" : ""));
            return null;
        }

        for (int i = 0; i < deviceCount; i++) {

            String fullDeviceDetails = "";

            //-> Fastest way to concatenate string in java (String tutorial - Jakob Jenkov).
            String[] params = new String[]{String.valueOf(i), getCurrentApiName(), getTargetDeviceType(), getFullDeviceDetails(i)};
            StringBuilder sb = new StringBuilder();

            for (int j = 0; j < params.length; j++) {
                sb.append(params[j]);
                if (j < params.length - 1)
                    sb.append("|");
            }
            //<-

            fullDeviceDetails = sb.toString();

            midiDevice = new MidiDevice(fullDeviceDetails);
            midiDevices.add(midiDevice);

            logger.info(fullDeviceDetails);
        }
        return midiDevices;
    }

    /**
     *
     */
    public boolean closeDevice() {
        try {
            lib.rtmidi_close_port(rtMidiDevice);
            if (rtMidiDevice.ok == 0) throw new MidiException();
            logger.info(getDeviceClassName() + " port ... closed");

        } catch (Throwable e) {
            if (rtMidiDevice.ok != 0) {
                System.out.println("Device not found - unable to close its port.");
            }
        }
        return rtMidiDevice.ok != 0;
    }

    /**
     *
     */
    public ArrayList<MidiDevice> getMidiDevices (boolean updateList) {
        // Update device list.
        if (updateList) {
            listDevices();
        }
        return this.midiDevices;
    }

    /**
     *
     */
    public MidiDevice getMidiDevice (int index, boolean updateList) {
        // Update device list.
        if (updateList) {
            listDevices();
        }

        MidiDevice md = this.midiDevices.get(index);
        return md;
    }



//    /**
//     *
//     */
//    public void displayErrorFromNative() {
//        String msgRaw = "";
//        String msg = "";
//
//        byte[] bbuf = this.midiDevice.errorMsg.getByteArray(0, 128);
//        msgRaw = new String(bbuf, StandardCharsets.UTF_8);
//        System.out.println("errorMsg raw: " + msgRaw);
//
//        bbuf = Arrays.copyOfRange(bbuf, 16, 128);
//        msg = new String(bbuf, StandardCharsets.UTF_8);
//        msg = msg.substring(0, msg.indexOf('\0'));
//
//        System.out.println("errorMsg cleaned: " + msg + "\nlength: " + msg.length());
//    }

//    /**
//     *
//     */
//    protected String getNativeMsg() {
//        String msg;
//        byte[] bbuf = this.midiDevice.errorMsg.getByteArray(0,128);
//        bbuf = Arrays.copyOfRange(bbuf, 16, 128);
//
//        msg = new String(bbuf, StandardCharsets.UTF_8);
////        return msg.substring(0, msg.indexOf('\0'));
//        return msg;
//    }
}
