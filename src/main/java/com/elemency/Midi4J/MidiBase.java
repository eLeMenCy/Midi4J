package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidiDevice;
import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import com.elemency.Midi4J.RtMidiDriver.RtMidiLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


public abstract class MidiBase implements AutoCloseable {
    protected final RtMidiLibrary lib = RtMidiLibrary.INSTANCE;
    private final Logger logger = LoggerFactory.getLogger(MidiBase.class);

//    protected MidiDevice midiDevice;

    protected RtMidiDevice rtMidiDevice = null;
    protected String deviceName = "Midi4J";
    protected String portName = "??";
    protected boolean isConnected = false;
    protected int targetDevicePortId = -1;

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
    private String getDeviceType() {
        return getDeviceClassName().equals("MidiIn") ? "In" : "Out";
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

        if (getDeviceClassName().equals("MidiIn")) {
            return lib.rtmidi_in_get_current_api(rtMidiDevice);
        }
        else {
            return lib.rtmidi_out_get_current_api(rtMidiDevice);
        }
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
    public void setDeviceName(String deviceName) {
        // TODO: exception.
        try {
            if (deviceName.isEmpty()) {
                throw new MidiException("setClientName() -> Client name cannot be empty!");
            }

            lib.rtmidi_set_client_name(this.rtMidiDevice, deviceName);
            this.deviceName = deviceName;

        }
        catch (MidiException msg) {
            logger.warn(msg.getMessage());
        }
    }

    /**
     *
     */
    public String getTargetDeviceName(int targetPortId) {
        String[] data = getFullDeviceDetails(targetPortId)/*.split("|")*/;

        if (data.length < 4) {
            return "Unknown";
        }

        return data[3];
    }

    /**
     *
     */
    public String getName() {
        return this.deviceName;
    }

    /**
     *
     */
    public String getPortName() {
        return this.portName;
    }

    /**
     *
     */
    public boolean isDeviceOpen() {
        return lib.rtmidi_is_port_open(this.rtMidiDevice);
    }

    /**
     *
     */
    public boolean connect(String portName, int toPortId, boolean autoConnect) {

        int deviceCount = getDeviceCount();
        boolean portIdIsValid = ((toPortId > -1) && (toPortId < deviceCount));

        System.out.println("");
        logger.info("Trying to " + (autoConnect ? "open and connect " : "open ") + "both " + getTargetDeviceName(toPortId) + " " +
                getTargetDeviceType() + " port (id " + toPortId + ") and " +
                this.deviceName + "'s " + portName + " port...");

        if (!portIdIsValid) {
            logger.warn(getTargetDeviceType() + " port (id " + toPortId + ") doesn't exist - " +
                    "Are the " + getCurrentApiName() + " Midi API and/or your Midi sw/hw running?");
        }

        String word1 = Misc.getFirstWord(this.deviceName);
        String word2 = Misc.getFirstWord(getTargetDeviceName(toPortId));

        if (word1.equals(word2)) {
            autoConnect = false;
        }

        lib.rtmidi_open_port(this.rtMidiDevice, toPortId, portName, autoConnect);

        if (this.rtMidiDevice.ok != 0) {
            String msg;

            if (portIdIsValid) {
                msg = this.deviceName + "'s " + portName +
                        " port " + (portIdIsValid ? "and " + getTargetDeviceName(toPortId) + "'s " + getTargetDeviceType() +
                        " port (id " + toPortId + ") have been opened succesfully" +
                        (autoConnect ? " and, at your request, connected together!" : " but, at your request, were left disconnected!") : "");
                        targetDevicePortId = toPortId;
                        isConnected = autoConnect;
                        this.portName = portName;

            } else {
                msg = "Couldn't find " + getTargetDeviceType() + " port (id " + toPortId + ") so only " +
                        this.deviceName + "'s " + portName + " port could be opened.";
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
     * @return
     */
    public String[] getFullDeviceDetails(int portId) {
        String fullDeviceDetails = "??";

        fullDeviceDetails = portId + "|" +
                getCurrentApiName() + "|" +
                getTargetDeviceType() + "|" +
                lib.rtmidi_get_port_name(this.rtMidiDevice, portId);

        String ids = Misc.findPattern(fullDeviceDetails,"\\w+:\\w+$");
        if (!ids.equals("")){
            fullDeviceDetails = fullDeviceDetails.replace((" " + ids), "");
            fullDeviceDetails += "|" + ids;
        }

        if (targetDevicePortId == portId && isConnected) {
            fullDeviceDetails += "|-->" + this.deviceName + "|" + this.portName + "|" + getDeviceType();
        }

        fullDeviceDetails = fullDeviceDetails.replace(":", "|");
//        System.out.println("fullDeviceDetails: " + fullDeviceDetails);

        return fullDeviceDetails.split("\\|");
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
    public String getTargetPortName(int targetPortId) {
        if (getDeviceCount() < 1) {
            return "No such a port";
        }

        String[] fullDeviceDetails = getFullDeviceDetails(targetPortId)/*.split("\\|")*/;
        if (fullDeviceDetails.length < 1 ) {
            return "??";
        }

        return fullDeviceDetails[1];
    }

    /**
     *
     * @return
     */
    public ArrayList<MidiDevice> listDevices() {
        int deviceCount = getDeviceCount();

        System.out.println("");
        if (deviceCount < 1) {
            logger.warn("There are no " + getCurrentApiName() + " Midi " +
                    getTargetDeviceType() + " ports" + (deviceCount > 1 ? "s" : "") + " available." +
                    (deviceCount == 0 ? " Are the " + getCurrentApiName()  + " API and/or your Midi sw/hw running?" : ""));
            return null;
        }

        // Build our list...
        ArrayList<MidiDevice> midiDevices = new ArrayList<>();
        for (int i = 0; i < deviceCount; i++) {

            String[] fullDeviceDetails = getFullDeviceDetails(i);

            //-> A Fast way to concatenate string in Java (String tutorial - Jakob Jenkov).
            StringBuilder sb = new StringBuilder();

            // Build a string with each array elements separated by '|' except for lat one.
            for (int j = 0; j < fullDeviceDetails.length; j++) {

                sb.append(fullDeviceDetails[j]);
                if (j < fullDeviceDetails.length - 1)
                    sb.append("|");
            }
            //<-

            /* Remove current device and its target from the list to minimise the temptation of doing a midi loop
            * (this has also been done in the connect method to avoid auto connection of these together) */
            if (fullDeviceDetails[3].equals(this.deviceName)) {
                continue;
            }

//            midiDevices.add(new MidiDevice(rtMidiDevice, fullDeviceDetails));
            logger.info(sb.toString());
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
