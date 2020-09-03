package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidi;
import com.elemency.Midi4J.RtMidiDriver.RtMidiDevice;
import com.elemency.Midi4J.RtMidiDriver.RtMidiLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class MidiDevice implements AutoCloseable {
    protected final RtMidiLibrary lib = RtMidiLibrary.INSTANCE;
    private final Logger logger = LoggerFactory.getLogger(MidiDevice.class);

//    protected MidiDevice midiDevice;

    protected RtMidiDevice rtMidiDevice = null;
    protected UUID uuid = UUID.randomUUID();
    protected String sourceDeviceName = "Midi4J";
    protected String sourcePortName = "??";
    protected Map<Integer, Boolean> connectedTargets = new LinkedHashMap<>();

    /**
     * Return a wrapped native Mididevice.
     *
     * @return rtMidiDevice
     */
    public RtMidiDevice getRtMidiDevice() {
        return rtMidiDevice;
    }

    /**
     * Return current source device object UUID.
     *
     * @return uuid
     */
    public UUID getSourceDeviceUUID() {
        return uuid;
    }

    /**
     * Return the class name of the current midi device instance.
     *
     * @return class name stripped from its .class extension.
     */
    protected String getSourceDeviceClassName() {
        String deviceClassName = this.getClass().getTypeName();
        return deviceClassName.substring(deviceClassName.lastIndexOf(".") + 1);
    }

    /**
     * Return the targeted type of the current midi device instance.
     *
     * @return  'In' when current instance is an OUT device
     *          'Out' when current instance is an IN device.
     */
    public String getTargetDeviceType() {
        return getSourceDeviceClassName().equals("MidiIn") ? "Out" : "In";
    }

    /**
     * Return the type of the current midi device instance.
     *
     * @return  'In' when current instance is an IN device
     *          'Out' when current instance is an OUT device.
     */
    public String getSourceDeviceType() {
        return getSourceDeviceClassName().equals("MidiIn") ? "In" : "Out";
    }

    /**
     * @param type          Error type
     * @param errorString   Error string
     */
    public void error(int type, String errorString) {
        lib.rtmidi_error(type, errorString);
    }

    /**
     * Return the midi api ID used by current device instance.
     *
     * @return the midi api ID used by current device instance.
     */
    public abstract int getCurrentApiId();

    /**
     * Return the midi api name used by current device instance.
     *
     * @return the midi api name used by current device instance.
     */
    public String getCurrentApiName() {
        return new RtMidi().getApiLabel(getCurrentApiId());
    }

    /**
     * Return the name of the target device based on ID
     *
     * @param targetDeviceId Target device ID.
     * @return Name of target device based on its ID.
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
     * Return the name of the current source device instance.
     *
     * @return the name of the current source device instance.
     */
    public String getSourceDeviceName() {
        return this.sourceDeviceName;
    }

    /**
     * Set the name of the current source device instance to a new one.
     *
     * @param sourceDeviceName Name to which the current source device will be renamed.
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
     * Return the name of the current source device instance port.
     *
     * @return The name of the current source device instance port.
     */
    public String getSourcePortName() {
        return this.sourcePortName;
    }

    /**
     * Set the name of the current source device instance port to a new one.
     *
     * @param sourcePortName Name to which the current source device port will be renamed.
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
     * @return true if the current source device has been opened.
     */
    public boolean isSourceDeviceOpen() {

        if (rtMidiDevice == null) {
            throw new NullPointerException("This device is null, can't check if its opened status.");
        }

        return lib.rtmidi_is_port_open(rtMidiDevice);
    }

    /**
     * Free the native memory used byt this source device instance.
     */
    public abstract void freeMemory();

    /**
     * Attempt to connect the current source device instance to a target device.
     *
     * @param sourcePortName    Name to be applied to the current source device port
     * @param toTargetPortId    Target device port id to connect to.
     *
     * @param autoConnect       Set to 'true' will open both devices and attempt to connect
     *                          their respective ports together.
     *                          Set to 'false' will open both devices but will be left unconnected.
     *
     * @return                  True if operation succeed, False otherwise.
     */
    public boolean connect(String sourcePortName, int toTargetPortId, boolean autoConnect) {

        if (rtMidiDevice == null) {
            throw new NullPointerException("This device is null, can't connect it to its target.");
        }

        int deviceCount = getTargetDeviceCount();
        boolean portIdIsValid = ((toTargetPortId > -1) && (toTargetPortId < deviceCount));

        System.out.println();
        logger.info("Trying to " + (autoConnect ? "open and connect " : "open ") +
                "both " + getTargetDeviceName(toTargetPortId) + " " +
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

        // Only 1 target OUT device can be connected to a IN device.
        if (getSourceDeviceType().equals("In") && !connectedTargets.isEmpty()) {
            connectedTargets.clear();
            lib.rtmidi_close_port(rtMidiDevice);
            logger.warn("You are trying to connect more than 1 OUT device to a IN device.");
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

                connectedTargets.put(toTargetPortId, autoConnect);
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
     * Open a virtual source device.
     * @param sourceDeviceName  name of the source device
     * @return boolean
     */
    public boolean openVirtualDevice(String sourceDeviceName) {

        if (rtMidiDevice.ok != 0) {
            lib.rtmidi_open_virtual_port(rtMidiDevice, sourceDeviceName);
        } else {
            System.out.println("Virtual device not opened");
        }
        return rtMidiDevice.ok != 0;
    }

    /**
     * @return the number of available target devices (compatible) with the current device instance.
     */
    public int getTargetDeviceCount() {
        if (rtMidiDevice == null) {
            throw new NullPointerException("This device is null, can't count its possible targets.");
        }

        return lib.rtmidi_get_port_count(rtMidiDevice);
    }

    /**
     * Collect and store the full details of a target device based on its port ID.
     *
     * @param id The id of the target device port.
     * @return the selected target device full details.
     */
    public Map<String, String> getTargetDeviceFullDetails(int id) {
// -------------------------------------------------------------------------------------------------------------------------------------------------------
// |     id       | apiName | targetPortType | targetDeviceName |   targetPortName    | targetDeviceId | targetPortId | deviceName | portName | portType |
// |--------------|---------|----------------|------------------|-----i----------------|----------------|--------------|------------|----------|----------|
// |      0       |  ALSA   |     Out/In     |   Midi Through   | Midi Through Port-0 |       14       |      0       |-->  Midi4J |    In    |    IN    |
// |--------------|---------|----------------|------------------|---------------------|----------------|--------------|------------|----------|----------|
// |      1       |  Jack   |     Out/In     | Calf Studio Gear |    Organ MIDI In    |       --       |      --      |-->  Midi4J |    Out   |    OUT   |
// -------------------------------------------------------------------------------------------------------------------------------------------------------

        if (rtMidiDevice == null) {
            throw new NullPointerException("This device is null, can't get its details.");
        }

        Map<String, String> fullDeviceDetails = new LinkedHashMap<>();

        fullDeviceDetails.put("id", Integer.toString(id));
        fullDeviceDetails.put("apiName", getCurrentApiName());
        fullDeviceDetails.put("targetPortType", getTargetDeviceType());

        String data = lib.rtmidi_get_port_name(rtMidiDevice, id);

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

        // Add Source device name/port to which this target device/port is connected.
        if (connectedTargets.containsKey(id)) {
            if (connectedTargets.get(id)) {
                fullDeviceDetails.put("sourceDeviceName", (getSourceDeviceType().equals("In") ? "-->" : "<--") + this.sourceDeviceName);
                fullDeviceDetails.put("sourcePortName", this.sourcePortName);
                fullDeviceDetails.put("sourcePortType", getSourceDeviceType());
            }
        }

        return fullDeviceDetails;
    }

    /**
     * Return the name of a target device port based on its ID.
     *
     * @param targetDevicePortId The id of the target device port.
     * @return the selected target device full details.
     */
    public String getTargetPortName(int targetDevicePortId) {

        String result = "! UNKNOWN PORT !";

        if (getTargetDeviceCount() < targetDevicePortId || targetDevicePortId < 0) {
            throw new MidiException(result + " - Given device id (" + targetDevicePortId + ") is outside current range of devices!");
        }

        result = getTargetDeviceFullDetails(targetDevicePortId).get("targetPortName");

        if (result == null || result.isEmpty()) {
            throw new MidiException(" - Port of target device ID (" + targetDevicePortId + ") is null or empty!");
        }

        return result;
    }

    /**
     * List all available target devices, for the current source device instance, together with their full details.
     *
     * @return the selected target device full detail list.
     */
    public List<Map<String, String>> listTargetDevices(boolean connectedOnly) {

// -------------------------------------------------------------------------------------------------------------------------------------------------------
// | midiDeviceId | apiName | targetPortType | targetDeviceName |   targetPortName    | targetDeviceId | targetPortId | deviceName | portName | portType |
// |--------------|---------|----------------|------------------|---------------------|----------------|--------------|------------|----------|----------|
// |      0       |  ALSA   |     Out/In     |   Midi Through   | Midi Through Port-0 |       14       |      0       |-->  Midi4J |    In    |    IN    |
// |--------------|---------|----------------|------------------|---------------------|----------------|--------------|------------|----------|----------|
// |      1       |  Jack   |     Out/In     | Calf Studio Gear |    Organ MIDI In    |       --       |      --      |-->  Midi4J |    Out   |    OUT   |
// -------------------------------------------------------------------------------------------------------------------------------------------------------

        int deviceCount = getTargetDeviceCount();
        boolean srcNameIsTgt = sourceNameIsTarget(0);

        System.out.println();
        if (deviceCount < 1 || srcNameIsTgt) {
            logger.warn("There are no " + getCurrentApiName() + " Midi " +
                    getTargetDeviceType() + " ports" + (deviceCount > 1 ? "s" : "") + " available." +
                    (deviceCount == 0 || srcNameIsTgt ? " Are the " + getCurrentApiName() + " API and/or your Midi sw/hw running?" : ""));
            return null;
        }

        // Build our device list and display log.
        List<Map<String, String>> midiDevices = new ArrayList<>();
        for (int i = 0; i < deviceCount; i++) {

            Map<String, String> fullDeviceDetails = getTargetDeviceFullDetails(i);

            // Build a logMsg with each map elements separated by '|'.
            StringBuilder logMsg = new StringBuilder();
            for (String value : fullDeviceDetails.values()) {
                if (value.equals("--"))
                    continue;
                logMsg.append(value).append("|");
            }

            /* Remove current device and its target from the list to minimise the temptation of doing a midi loop
             * (this has also been done in the connect method to avoid auto connection */
            if (sourceNameIsTarget(i) || (connectedOnly && !fullDeviceDetails.containsKey("sourceDeviceName"))) {
                continue;
            }

            midiDevices.add(fullDeviceDetails);
            logger.info(logMsg.toString());
        }

        if (midiDevices.isEmpty())
            logger.info("There are no " + getTargetDeviceType().toUpperCase() + " target devices connected to " + this.sourceDeviceName);

        return midiDevices;
    }

    /**
     * Check that current device instance in and out ports stay disconnected to avoid a midi loop.
     *
     * @param targetDeviceId  The id of the target device port.
     * @return              True if target device name is the same as current device instance name, false otherwise.
     */
    private boolean sourceNameIsTarget(int targetDeviceId) {
        // To Minimise Midi loops, bypasses current Midi4J source device to be listed as a possible target devices.
        String tgtName = getTargetDeviceFullDetails(targetDeviceId).get("targetDeviceName");
        return (tgtName != null) && (tgtName.contains(this.sourceDeviceName));
    }

    /**
     * Close the current source device instance.
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
