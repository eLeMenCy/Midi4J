package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidiDevice;

import java.util.Map;

public class MidiDevice {
    private RtMidiDevice rtMidiDevice = null;

    private String apiName = "Unknown";

    private String midiDeviceId = "--";

    private String targetDeviceId = "--";
    private String targetDeviceName = "--";

    private boolean isConnected = false;

//    private String ctdDeviceId = "--";
//    private String ctdPortId = "--";

    private String deviceName = "--";
    private String portName = "--";
    private String portType = "--";
    
    private String targetPortId = "--";
    private String targetPortName = "--";
    private String targetPortType = "--";


    public MidiDevice() {

    }

    //    public MidiDevice(String[] fullDeviceDetails) {
    public MidiDevice(RtMidiDevice rtMidiDevice, Map<String, String> params) {
        int connectedIndex = -1;

        this.rtMidiDevice = rtMidiDevice;

// -------------------------------------------------------------------------------------------------------------------------------------------------------
// | midiDeviceId | apiName | portType | deviceName | portName | targetPortType | targetDeviceName |    targetPortName   | targetDeviceId | targetPortId |
// |--------------|---------|----------|------------|----------|----------------|------------------|---------------------|----------------|--------------|
// |      0       |  ALSA   |    IN    |   Midi4J   |    In    |-->    OUT      |   Midi Through   | Midi Through Port-0 |       14       |      0       |
// |--------------|---------|----------|------------|----------|----------------|------------------|---------------------|----------------|--------------|
// |      0       |  Jack   |    OUT   |   Midi4J   |    Out   |<--    IN       | Calf Studio Gear |    Organ MIDI In    |       --       |      --      |
// -------------------------------------------------------------------------------------------------------------------------------------------------------

// -------------------------------------------------------------------------------------------------------------------------------------------------------
// | midiDeviceId | apiName | targetPortType | targetDeviceName |   targetPortName    | targetDeviceId | targetPortId | deviceName | portName | portType |
// |--------------|---------|----------------|------------------|---------------------|----------------|--------------|------------|----------|----------|
// |      0       |  ALSA   |     Out/In     |   Midi Through   | Midi Through Port-0 |       14       |      0       |-->  Midi4J |    In    |    IN    |
// |--------------|---------|----------------|------------------|---------------------|----------------|--------------|------------|----------|----------|
// |      1       |  Jack   |     Out/In     | Calf Studio Gear |    Organ MIDI In    |       --       |      --      |-->  Midi4J |    Out   |    OUT   |
// -------------------------------------------------------------------------------------------------------------------------------------------------------

        midiDeviceId = params.get("midiDeviceId");
        apiName = params.get("apiName");
        targetPortType = params.get("targetPortType");
        targetDeviceName = params.get("targetDeviceName");
        targetPortName = params.get("targetPortName");
        targetDeviceId = params.get("targetDeviceId");
        targetPortId = params.get("targetPortId");

        if (isConnected = (params.size() > 7)) {
//            deviceId = params.get("deviceId");
//            portId = params.get("portId");
            deviceName = params.get("deviceName");
            portName = params.get("portName");
            portType = params.get("portType");
        }

//        for (String value : params.values()) {
//            System.out.println("Values: " + value);
//        }
    }

    public RtMidiDevice getRtMidiDevice() {
        return rtMidiDevice;
    }

    public void setRtMidiDevice(RtMidiDevice rtMidiDevice) {
        this.rtMidiDevice = rtMidiDevice;
    }

    public String getTargetPortType() {
        return targetPortType;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getPortName() {
        return portName;
    }

    public String getMidiDeviceId() {
        return midiDeviceId;
    }

    public String getApiName() {
        return apiName;
    }

    public String getTargetDeviceId() {
        return targetDeviceId;
    }

    public String getTargetDeviceName() {
        return targetDeviceName;
    }

    public String getTargetPortId() {
        return targetPortId;
    }

    public String getTargetPortName() {
        return targetPortName;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

//    public String getCtdDeviceId() {
//        return ctdDeviceId;
//    }
//
//    public String getCtdPortId() {
//        return ctdPortId;
//    }
//
//    public void setCtdDeviceId(String ctdDeviceId) {
//        this.ctdDeviceId = ctdDeviceId;
//    }
//
//    public void setCtdPortId(String ctdPortId) {
//        this.ctdPortId = ctdPortId;
//    }
}



/* excerpt lsl

        list params = llParseString2List(message, ["|"],[]);

        string command = llList2String(params, 0);
        integer btnStatus = llList2Integer(params, 1);
        string data2 = llList2String(params, 2);

        //llOwnerSay("rcv: " + command + ", btnStatus: " + (string)btnStatus + ", data2: " + data2);
        //llOwnerSay("gb_btnStatus: " + (string)gb_btnStatus);

        if (command == "Click")
        {
            gb_btnStatus = btnStatus;
            if (btnStatus == 0)
            {
                // Start (fade in) fire with random delay.
                llSetTimerEvent(gf_RandDelay);
            }
            else
            {
                // Turn off (fadedown) fire
                SetFlame(1);
            }
        }
        else if (command == "Reset")
        {
            // Turn off (fadedown) fire
            SetFlame(1);
            gb_btnStatus = TRUE;
        }
        else if (command == "Wind" && gb_btnStatus == 0)
        {
            SetSmoke(0); // Update smoke directtion
        }
    }
 */