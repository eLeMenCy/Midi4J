package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import com.elemency.Midi4J.RtMidiDriver.RtMidiDevice;
import com.elemency.Midi4J.RtMidiDriver.RtMidiLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


//TODO: Refactor all device variable name other than those containing target to source
// (i.e. deviceName -> sourceDeviceName, portName -> sourcePortName etc...)

public abstract class MidiDeviceMgr implements AutoCloseable {
    protected final RtMidiLibrary lib = RtMidiLibrary.INSTANCE;
    private final Logger logger = LoggerFactory.getLogger(MidiDeviceMgr.class);

//    protected MidiDevice midiDevice;

    protected RtMidiDevice rtMidiDevice = null;
    protected String sourceDeviceName = "Midi4J";
    protected String sourcePortName = "??";
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
    protected String getSourceDeviceClassName() {
        String deviceClassName = this.getClass().getTypeName();
        return deviceClassName.substring(deviceClassName.lastIndexOf(".") + 1);
    }

    /**
     *
     */
    public String getTargetDeviceType() {
        return getSourceDeviceClassName().equals("MidiIn") ? "Out" : "In";
    }

    /**
     * @return
     */
    public String getSourceDeviceType() {
        return getSourceDeviceClassName().equals("MidiIn") ? "In" : "Out";
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

        if (getTargetDeviceCount() < targetDeviceId || targetDeviceId < 0) {
            throw new MidiException("Given device id (" + targetDeviceId + ") is outside current range of devices!");
        }

        String result = getTargetDeviceFullDetails(targetDeviceId).get("targetDeviceName");

        if (result == null || result.isEmpty()) {
            throw new MidiException(" - Target device ID (" + targetDeviceId + ") is null or empty!");
        }

        return result;
    }

    /**
     * @return
     */
    public String getSourceDeviceName() {
        return this.sourceDeviceName;
    }

    /**
     * @param sourceDeviceName
     */
    public void setSourceDeviceName(String sourceDeviceName) {
        if (rtMidiDevice == null) {
            throw new NullPointerException("This device is null - name can't be changed.");
        }

        String name = sourceDeviceName;

        if (sourceDeviceName.isEmpty()) {
            name = "Midi4J";
        }

        lib.rtmidi_set_client_name(rtMidiDevice, name);
        this.sourceDeviceName = name;
    }

    /**
     * @return
     */
    public String getSourcePortName() {
        return this.sourcePortName;
    }

    /**
     * @param sourcePortName
     */
    public void setSourcePortName(String sourcePortName) {
        if (rtMidiDevice == null) {
            throw new NullPointerException("This device is null, can't set its port name.");
        }

        if (sourcePortName.isEmpty()) {
            sourcePortName = getSourceDeviceType().toUpperCase();
            logger.warn("A Port name can't be empty! It has been named '" + sourcePortName + "' (default port name)");
        }
        this.sourcePortName = sourcePortName;
        lib.rtmidi_set_port_name(rtMidiDevice, sourcePortName);
    }

    /**
     * @return
     */
    public boolean isSourceDeviceOpen() {

        if (rtMidiDevice == null) {
            throw new NullPointerException("This device is null, can't check if its opened status.");
        }

        return lib.rtmidi_is_port_open(rtMidiDevice);
    }

    /**
     *
     */
    public abstract void freeMemory();

    /**
     * @param sourcePortName
     * @param toTargetPortId
     * @param autoConnect
     * @return
     */
    public boolean connect(String sourcePortName, int toTargetPortId, boolean autoConnect) {

        if (rtMidiDevice == null) {
            throw new NullPointerException("This device is null, can't connect it to its target.");
        }

        int deviceCount = getTargetDeviceCount();
        boolean portIdIsValid = ((toTargetPortId > -1) && (toTargetPortId < deviceCount));

        System.out.println();
        logger.info("Trying to " + (autoConnect ? "open and connect " : "open ") + "both " + getTargetDeviceName(toTargetPortId) + " " +
                getTargetDeviceType() + " port (id " + toTargetPortId + ") and " +
                this.sourceDeviceName + "'s " + sourcePortName + " port...");

        if (!portIdIsValid) {
            logger.warn("..." + getTargetDeviceType() + " port (id " + toTargetPortId + ") doesn't exist - " +
                    "Are the " + getCurrentApiName() + " Midi API and/or your Midi sw/hw running?");
        }

        // Avoid looping I/O ports of same device.
        String devName = Misc.getFirstWord(this.sourceDeviceName);
        String tgtDevName = Misc.getFirstWord(getTargetDeviceName(toTargetPortId));
        if (devName.equals(tgtDevName)) {
            autoConnect = false;
        }

        sourcePortName = sourcePortName.isEmpty() ? getSourceDeviceType().toUpperCase() : sourcePortName;
        lib.rtmidi_open_port(rtMidiDevice, toTargetPortId, sourcePortName, autoConnect);

        if (rtMidiDevice.ok != 0) {
            String msg;

            if (portIdIsValid) {
                msg = this.sourceDeviceName + "'s " + sourcePortName +
                        " port and " + getTargetDeviceName(toTargetPortId) + "'s " + getTargetDeviceType() +
                        " port (id " + toTargetPortId + ") have been opened succesfully" +
                        (autoConnect ? " and, at your request, connected together!" : " but, at your request, were left disconnected!");
                targetDevicePortId = toTargetPortId;
                isConnected = autoConnect;
                this.sourcePortName = sourcePortName;

            } else {
                msg = "Couldn't find " + getTargetDeviceType() + " port (id " + toTargetPortId + ") so only " +
                        this.sourceDeviceName + "'s " + sourcePortName + " port has been opened.";
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
    public boolean openVirtualDevice(String sourceDeviceName) {

        if (rtMidiDevice.ok != 0) {
            lib.rtmidi_open_virtual_port(rtMidiDevice, sourceDeviceName);
        } else {
            System.out.println("Virtual Port not opened");
        }
        return rtMidiDevice.ok != 0;
    }

    /**
     *
     */
    public int getTargetDeviceCount() {
        if (rtMidiDevice == null) {
            throw new NullPointerException("This device is null, can't count its possible targets.");
        }

        return lib.rtmidi_get_port_count(rtMidiDevice);
    }

    /**
     * @param portId
     * @return
     */
    public Map<String, String> getTargetDeviceFullDetails(int portId) {
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
            fullDeviceDetails.put("sourceDeviceName", (getSourceDeviceType().equals("In") ? "-->" : "<--") + this.sourceDeviceName);
            fullDeviceDetails.put("sourcePortName", this.sourcePortName);
            fullDeviceDetails.put("sourcePortType", getSourceDeviceType());
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

        if (getTargetDeviceCount() < targetDeviceId || targetDeviceId < 0) {
            throw new MidiException(result + " - Given device id (" + targetDeviceId + ") is outside current range of devices!");
        }

        result = getTargetDeviceFullDetails(targetDeviceId).get("targetPortName");

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

        int deviceCount = getTargetDeviceCount();
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

            Map<String, String> fullDeviceDetails = getTargetDeviceFullDetails(i);

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
        // To Minimise Midi loops, bypasses current Midi4J source device to be listed as a possible target devices.
        String tgtName = getTargetDeviceFullDetails(port).get("targetDeviceName");
        return (tgtName != null) && (tgtName.contains(this.sourceDeviceName));
    }

    /**
     *
     */
    public void closeSourceDevice() {
        if (rtMidiDevice == null) {
            throw new NullPointerException("This device is null and can't be closed.");
        }

        lib.rtmidi_close_port(rtMidiDevice);
        logger.info(getSourceDeviceClassName() + "(" + getSourceDeviceName() + ") " + "device ... closed");
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
