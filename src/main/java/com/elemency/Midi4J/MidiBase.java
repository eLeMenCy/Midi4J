package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidiDevice;
import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import com.elemency.Midi4J.RtMidiDriver.RtMidiLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


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
    public abstract int getCurrentApiId();

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
    public String getTargetDeviceName(int toPorId) {
        if (getDeviceCount() < toPorId) {
            return "unknown device";
        }

        Map<String, String> data = getFullDeviceDetails(toPorId);

        if (data.get("targetDeviceName") == null) {
            return "unknown";
        }

        return data.get("targetDeviceName");
    }

    /**
     *
     */
    public String getDeviceName() {
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
     *
     */
    public abstract void free();


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
            logger.warn("..." + getTargetDeviceType() + " port (id " + toPortId + ") doesn't exist - " +
                    "Are the " + getCurrentApiName() + " Midi API and/or your Midi sw/hw running?");
        }

        // Avoid connecting I/O ports of same device.
        String devName = Misc.getFirstWord(this.deviceName);
        String tgtDevName = Misc.getFirstWord(getTargetDeviceName(toPortId));

        if (devName.equals(tgtDevName)) {
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
    public Map<String, String> getFullDeviceDetails(int portId) {
// -------------------------------------------------------------------------------------------------------------------------------------------------------
// | midiDeviceId | apiName | targetPortType | targetDeviceName |   targetPortName    | targetDeviceId | targetPortId | deviceName | portName | portType |
// |--------------|---------|----------------|------------------|---------------------|----------------|--------------|------------|----------|----------|
// |      0       |  ALSA   |     Out/In     |   Midi Through   | Midi Through Port-0 |       14       |      0       |-->  Midi4J |    In    |    IN    |
// |--------------|---------|----------------|------------------|---------------------|----------------|--------------|------------|----------|----------|
// |      1       |  Jack   |     Out/In     | Calf Studio Gear |    Organ MIDI In    |       --       |      --      |-->  Midi4J |    Out   |    OUT   |
// -------------------------------------------------------------------------------------------------------------------------------------------------------

        Map<String, String> fullDeviceDetails = new LinkedHashMap<>();

        fullDeviceDetails.put("midiDeviceId", Integer.toString(portId));
        fullDeviceDetails.put("apiName", getCurrentApiName());
        fullDeviceDetails.put("targetPortType", getTargetDeviceType());

        String data = lib.rtmidi_get_port_name(this.rtMidiDevice, portId);

        int semicolonIndex = data.indexOf(":");
        if (semicolonIndex > -1) {
            fullDeviceDetails.put("targetDeviceName", data.substring(0, semicolonIndex));
            fullDeviceDetails.put("targetPortName", data.substring(semicolonIndex + 1));
        }
        fullDeviceDetails.put("targetDeviceId", "--");
        fullDeviceDetails.put("targetPortId", "--");

        String ids = Misc.findPattern(data,"\\w+:\\w+$");
        if (!ids.equals("")){
            data = data.replace((" " + ids), "");

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
        if (getDeviceCount() < targetPortId) {
            return "unknown port";
        }

        Map<String, String> fullDeviceDetails = getFullDeviceDetails(targetPortId);

        if (fullDeviceDetails.get("portName") == null) {
            return "unknown";
        }

        return fullDeviceDetails.get("portName");
    }

    /**
     *
     * @return
     */
    public ArrayList<MidiDevice> listDevices() {

// -------------------------------------------------------------------------------------------------------------------------------------------------------
// | midiDeviceId | apiName | targetPortType | targetDeviceName |   targetPortName    | targetDeviceId | targetPortId | deviceName | portName | portType |
// |--------------|---------|----------------|------------------|---------------------|----------------|--------------|------------|----------|----------|
// |      0       |  ALSA   |     Out/In     |   Midi Through   | Midi Through Port-0 |       14       |      0       |-->  Midi4J |    In    |    IN    |
// |--------------|---------|----------------|------------------|---------------------|----------------|--------------|------------|----------|----------|
// |      1       |  Jack   |     Out/In     | Calf Studio Gear |    Organ MIDI In    |       --       |      --      |-->  Midi4J |    Out   |    OUT   |
// -------------------------------------------------------------------------------------------------------------------------------------------------------

        int deviceCount = getDeviceCount();
        String tgtName = getFullDeviceDetails(0).get("targetDeviceName");
        boolean tgtNameIsName = (tgtName != null) && (tgtName.contains(this.deviceName));

        System.out.println("");
        if (deviceCount < 1 || tgtNameIsName) {
            logger.warn("There are no " + getCurrentApiName() + " Midi " +
                    getTargetDeviceType() + " ports" + (deviceCount > 1 ? "s" : "") + " available." +
                    (deviceCount == 0 || tgtNameIsName ? " Are the " + getCurrentApiName()  + " API and/or your Midi sw/hw running?" : ""));
            return null;
        }

        // Build our list as a map. TODO: refactor the midiDevices arraylist to a map.
        ArrayList<MidiDevice> midiDevices = new ArrayList<>();
        for (int i = 0; i < deviceCount; i++) {

            Map<String, String> fullDeviceDetails = getFullDeviceDetails(i);

            // Build a logMsg with each array elements separated by '|'.
            StringBuilder logMsg = new StringBuilder();
            for (String value : fullDeviceDetails.values()) {
                if (value.equals("--"))
                    continue;
                logMsg.append(value + "|");
            }

            /* Remove current device and its target from the list to minimise the temptation of doing a midi loop
            * (this has also been done in the connect method to avoid auto connection of these together) */
            if (fullDeviceDetails.get("targetDeviceName").contains(this.deviceName)) {
                continue;
            }

            midiDevices.add(new MidiDevice(rtMidiDevice, fullDeviceDetails));
            logger.info(logMsg.toString());
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
            logger.info(getDeviceClassName() + "(" + getDeviceName() + ") " + "device ... closed");

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
