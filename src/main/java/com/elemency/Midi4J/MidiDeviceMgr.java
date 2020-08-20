package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import com.elemency.Midi4J.RtMidiDriver.RtMidiDevice;
import com.elemency.Midi4J.RtMidiDriver.RtMidiLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


//TODO: Merge the MidiDevice CLass with this one and then delete MidiDevice.
// Refactor all variable name other than those containing target to source
// (i.e. deviceName -> sourceDeviceName, portName -> sourcePortName etc...)

public abstract class MidiDeviceMgr implements AutoCloseable {
    protected final RtMidiLibrary lib = RtMidiLibrary.INSTANCE;
    private final Logger logger = LoggerFactory.getLogger(MidiDeviceMgr.class);

//    protected MidiDevice midiDevice;

    protected RtMidiDevice rtMidiDevice = null;
    protected String deviceName = "Midi4J";
    protected String portName = "??";
    protected boolean isConnected = false;
    protected int targetDevicePortId = -1;

    /**
     *
     */
    public RtMidiDevice getRtMidiDevice() {
        return rtMidiDevice;
    }

    /**
     *
     */
    protected String getDeviceClassName() {
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
     * @return
     */
    private String getDeviceType() {
        return getDeviceClassName().equals("MidiIn") ? "In" : "Out";
    }

    /**
     * @param type
     * @param errorString
     */
    public void error(int type, String errorString) {
        lib.rtmidi_error(type, errorString);
    }

    /**
     *
     */
    public abstract int getCurrentApiId();

    /**
     * @return
     */
    public String getCurrentApiName() {
        return new RtMidi().getApiLabel(getCurrentApiId());
    }

    /**
     * @param targetDeviceId
     * @return
     */
    public String getTargetDeviceName(int targetDeviceId) {

        if (getDeviceCount() < targetDeviceId || targetDeviceId < 0) {
            throw new MidiException("Given device id (" + targetDeviceId + ") is outside current range of devices!");
        }

        String result = getFullDeviceDetails(targetDeviceId).get("targetDeviceName");

        if (result == null || result.isEmpty()) {
            throw new MidiException(" - Target device ID (" + targetDeviceId + ") is null or empty!");
        }

        return result;
    }

    /**
     * @return
     */
    public String getDeviceName() {
        return this.deviceName;
    }

    /**
     * @param deviceName
     */
    public void setDeviceName(String deviceName) {
        if (rtMidiDevice == null) {
            throw new NullPointerException("This device is null - name can't be changed.");
        }

        String name = deviceName;

        if (deviceName.isEmpty()) {
            name = "Midi4J";
        }

        lib.rtmidi_set_client_name(rtMidiDevice, name);
        this.deviceName = name;
    }

    /**
     * @return
     */
    public String getPortName() {
        return this.portName;
    }

    /**
     * @param portName
     */
    public void setPortName(String portName) {
        if (rtMidiDevice == null) {
            throw new NullPointerException("This device is null, can't set its port name.");
        }

        if (portName.isEmpty()) {
            portName = getDeviceType().toUpperCase();
            logger.warn("A Port name can't be empty! It has been named '" + portName + "' (default port name)");
        }

        lib.rtmidi_set_port_name(rtMidiDevice, portName);
    }

    /**
     * @return
     */
    public boolean isDeviceOpen() {

        if (rtMidiDevice == null) {
            throw new NullPointerException("This device is null, can't check if its opened status.");
        }

        return lib.rtmidi_is_port_open(rtMidiDevice);
    }

    /**
     *
     */
    public abstract void free();

    /**
     * @param portName
     * @param toPortId
     * @param autoConnect
     * @return
     */
    public boolean connect(String portName, int toPortId, boolean autoConnect) {

        if (rtMidiDevice == null) {
            throw new NullPointerException("This device is null, can't connect it to its target.");
        }

        int deviceCount = getDeviceCount();
        boolean portIdIsValid = ((toPortId > -1) && (toPortId < deviceCount));

        System.out.println();
        logger.info("Trying to " + (autoConnect ? "open and connect " : "open ") + "both " + getTargetDeviceName(toPortId) + " " +
                getTargetDeviceType() + " port (id " + toPortId + ") and " +
                this.deviceName + "'s " + portName + " port...");

        if (!portIdIsValid) {
            logger.warn("..." + getTargetDeviceType() + " port (id " + toPortId + ") doesn't exist - " +
                    "Are the " + getCurrentApiName() + " Midi API and/or your Midi sw/hw running?");
        }

        // Avoid looping I/O ports of same device.
        String devName = Misc.getFirstWord(this.deviceName);
        String tgtDevName = Misc.getFirstWord(getTargetDeviceName(toPortId));
        if (devName.equals(tgtDevName)) {
            autoConnect = false;
        }

        portName = portName.isEmpty() ? getDeviceType().toUpperCase() : portName;
        lib.rtmidi_open_port(rtMidiDevice, toPortId, portName, autoConnect);

        if (rtMidiDevice.ok != 0) {
            String msg;

            if (portIdIsValid) {
                msg = this.deviceName + "'s " + portName +
                        " port and " + getTargetDeviceName(toPortId) + "'s " + getTargetDeviceType() +
                        " port (id " + toPortId + ") have been opened succesfully" +
                        (autoConnect ? " and, at your request, connected together!" : " but, at your request, were left disconnected!");
                targetDevicePortId = toPortId;
                isConnected = autoConnect;
                this.portName = portName;

            } else {
                msg = "Couldn't find " + getTargetDeviceType() + " port (id " + toPortId + ") so only " +
                        this.deviceName + "'s " + portName + " port has been opened.";
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
            System.out.println("Virtual Port not opened");
        }
        return rtMidiDevice.ok != 0;
    }

    /**
     *
     */
    public int getDeviceCount() {
        if (rtMidiDevice == null) {
            throw new NullPointerException("This device is null, can't count its possible targets.");
        }

        return lib.rtmidi_get_port_count(rtMidiDevice);
    }

    /**
     * @param portId
     * @return
     */
    public Map<String, String> getFullDeviceDetails(int portId) {
// -------------------------------------------------------------------------------------------------------------------------------------------------------
// | midiDeviceId | apiName | targetPortType | targetDeviceName |   targetPortName    | targetDeviceId | targetPortId | deviceName | portName | portType |
// |--------------|---------|----------------|------------------|---------------------|----------------|--------------|------------|----------|----------|
// |      0       |  ALSA   |     Out/In     |   Midi Through   | Midi Through Port-0 |       14       |      0       |-->  Midi4J |    In    |    IN    |
// |--------------|---------|----------------|------------------|---------------------|----------------|--------------|------------|----------|----------|
// |      1       |  Jack   |     Out/In     | Calf Studio Gear |    Organ MIDI In    |       --       |      --      |-->  Midi4J |    Out   |    OUT   |
// -------------------------------------------------------------------------------------------------------------------------------------------------------

        if (rtMidiDevice == null) {
            throw new NullPointerException("This device is null, can't get its details.");
        }

        Map<String, String> fullDeviceDetails = new LinkedHashMap<>();

        fullDeviceDetails.put("apiName", getCurrentApiName());
        fullDeviceDetails.put("targetPortType", getTargetDeviceType());

        String data = lib.rtmidi_get_port_name(rtMidiDevice, portId);

        int semicolonIndex = data.indexOf(":");
        if (semicolonIndex > -1) {
            fullDeviceDetails.put("targetDeviceName", data.substring(0, semicolonIndex));
            fullDeviceDetails.put("targetPortName", data.substring(semicolonIndex + 1));
        }

        fullDeviceDetails.put("targetDeviceId", "--");
        fullDeviceDetails.put("targetPortId", "--");

        String ids = Misc.findPattern(data, "\\w+:\\w+$");
        if (!ids.equals("")) {
            data = data.replace((" " + ids), "");

            fullDeviceDetails.put("targetPortName", data.substring(semicolonIndex + 1));

            semicolonIndex = ids.indexOf(":");
            fullDeviceDetails.put("targetDeviceId", ids.substring(0, semicolonIndex));
            fullDeviceDetails.put("targetPortId", ids.substring(semicolonIndex + 1));
        }

        if (targetDevicePortId == portId && isConnected) {
            fullDeviceDetails.put("deviceName", (getDeviceType().equals("In") ? "-->" : "<--") + this.deviceName);
            fullDeviceDetails.put("portName", this.portName);
            fullDeviceDetails.put("portType", getDeviceType());
        }

        return fullDeviceDetails;
    }

    /**
     * @param targetDeviceId
     * @return
     */
    public String getTargetPortName(int targetDeviceId) {
//        if (getDeviceCount() < targetDeviceId) {
//            return "Port doesn't exist";
//        }
//
//        String tmp = getFullDeviceDetails(targetDeviceId).get("targetPortName");
//        if (tmp == null || tmp.isEmpty()) {
//            return "Port doesn't exist";
//        }
//
//        return tmp;

        String result = "! UNKNOWN PORT !";

        if (getDeviceCount() < targetDeviceId || targetDeviceId < 0) {
            throw new MidiException(result + " - Given device id (" + targetDeviceId + ") is outside current range of devices!");
        }

        result = getFullDeviceDetails(targetDeviceId).get("targetPortName");

        if (result == null || result.isEmpty()) {
            throw new MidiException(" - Port of target device ID (" + targetDeviceId + ") is null or empty!");
        }

        return result;
    }

    /**
     * @return
     */
    public List<Map<String, String>> listTargetDevices() {

// -------------------------------------------------------------------------------------------------------------------------------------------------------
// | midiDeviceId | apiName | targetPortType | targetDeviceName |   targetPortName    | targetDeviceId | targetPortId | deviceName | portName | portType |
// |--------------|---------|----------------|------------------|---------------------|----------------|--------------|------------|----------|----------|
// |      0       |  ALSA   |     Out/In     |   Midi Through   | Midi Through Port-0 |       14       |      0       |-->  Midi4J |    In    |    IN    |
// |--------------|---------|----------------|------------------|---------------------|----------------|--------------|------------|----------|----------|
// |      1       |  Jack   |     Out/In     | Calf Studio Gear |    Organ MIDI In    |       --       |      --      |-->  Midi4J |    Out   |    OUT   |
// -------------------------------------------------------------------------------------------------------------------------------------------------------

        int deviceCount = getDeviceCount();
        boolean tgtNameIsName = tgtNameIsName(0);

        System.out.println();
        if (deviceCount < 1 || tgtNameIsName) {
            logger.warn("There are no " + getCurrentApiName() + " Midi " +
                    getTargetDeviceType() + " ports" + (deviceCount > 1 ? "s" : "") + " available." +
                    (deviceCount == 0 || tgtNameIsName ? " Are the " + getCurrentApiName() + " API and/or your Midi sw/hw running?" : ""));
            return null;
        }

        // Build our device map.
        List<Map<String, String>> midiDevices = new ArrayList<>();
        for (int i = 0; i < deviceCount; i++) {

            Map<String, String> fullDeviceDetails = getFullDeviceDetails(i);

            // Build a logMsg with each array elements separated by '|'.
            StringBuilder logMsg = new StringBuilder();
            for (String value : fullDeviceDetails.values()) {
                if (value.equals("--"))
                    continue;
                logMsg.append(value).append("|");
            }

            /* Remove current device and its target from the list to minimise the temptation of doing a midi loop
             * (this has also been done in the connect method to avoid auto connection */
            if (tgtNameIsName(i)) {
                continue;
            }

            midiDevices.add(fullDeviceDetails);
            logger.info(logMsg.toString());
        }

        return midiDevices;
    }

    private boolean tgtNameIsName(int port) {
        // To Minimise Midi loops, bypasses current Midi4J device to be listed as a possible target devices.
        String tgtName = getFullDeviceDetails(port).get("targetDeviceName");
        return (tgtName != null) && (tgtName.contains(this.deviceName));
    }

    /**
     *
     */
    public void closeDevice() {
        if (rtMidiDevice == null) {
            throw new NullPointerException("This device is null and can't be closed.");
        }

        lib.rtmidi_close_port(rtMidiDevice);
        logger.info(getDeviceClassName() + "(" + getDeviceName() + ") " + "device ... closed");
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
