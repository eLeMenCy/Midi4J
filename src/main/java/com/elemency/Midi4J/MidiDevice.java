package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidiDevice;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MidiDevice {
    private RtMidiDevice rtMidiDevice = null;

    private String apiName = "Unknown";

    private String midiDeviceId = "--";

    private String deviceId = "--";
    private String deviceName = "--";

    private boolean isConnected = false;
    private String ctdDeviceId = "--";
    private String ctdDeviceName = "--";

    private String ctdPortId = "--";
    private String ctdPortName = "--";
    private String ctdPortType = "--";
    
    private String portId = "--";
    private String portName = "--";
    private String portType = "--";


    public MidiDevice() {

    }

    //    public MidiDevice(String[] fullDeviceDetails) {
    public MidiDevice(RtMidiDevice rtMidiDevice, Map<String, String> params) {
        int connectedIndex = -1;

        this.rtMidiDevice = rtMidiDevice;

// -----------------------------------------------------------------------------------------------------------------------------------------------
// | midiDeviceId | apiName | portType |    deviceName    |      portName       | deviceId | portId | ctdDeviceName | ctdPortName | ctdPortType  |
// |--------------|---------|----------|------------------|---------------------|----------|--------|---------------|-------------|--------------|
// |      0       |  ALSA   |  Out/In  |   Midi Through   | Midi Through Port-0 |    14    |   0    |-->   Midi4J   |     In      |     IN       |
// |--------------|---------|----------|------------------|---------------------|----------|--------|---------------|-------------|--------------|
// |      1       |  Jack   |  Out/In  | Calf Studio Gear |    Organ MIDI In    |    --    |   --   |-->  Midi4J    |     Out     |     OUT      |
// -----------------------------------------------------------------------------------------------------------------------------------------------

        midiDeviceId = params.get("midiDeviceId");
        apiName = params.get("apiName");
        portType = params.get("portType");
        deviceName = params.get("deviceName");
        portName = params.get("portName");
        deviceId = params.get("deviceId");
        portId = params.get("portId");

        if (isConnected = (params.size() > 7)) {
//            connectedDeviceId = Integer.parseInt(params[x]);
//            connectedPortId = Integer.parseInt(params[x]);
            ctdDeviceName = params.get("ctdDeviceName");
            ctdPortName = params.get("ctdPortName");
            ctdPortType = params.get("ctdPortType");
        }

        for (String value : params.values()) {
            System.out.println("Values: " + value);
        }
    }

    public RtMidiDevice getRtMidiDevice() {
        return rtMidiDevice;
    }

    public void setRtMidiDevice(RtMidiDevice rtMidiDevice) {
        this.rtMidiDevice = rtMidiDevice;
    }

    public String getPortType() {
        return portType;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String getCtdDeviceId() {
        return ctdDeviceId;
    }

    public String getCtdDeviceName() {
        return ctdDeviceName;
    }

    public String getCtdPortId() {
        return ctdPortId;
    }

    public String getCtdPortName() {
        return ctdPortName;
    }

    public String getMidiDeviceId() {
        return midiDeviceId;
    }

    public String getApiName() {
        return apiName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getPortId() {
        return portId;
    }

    public String getPortName() {
        return portName;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public void setCtdDeviceId(String ctdDeviceId) {
        this.ctdDeviceId = ctdDeviceId;
    }

    public void setCtdDeviceName(String ctdDeviceName) {
        this.ctdDeviceName = ctdDeviceName;
    }

    public void setCtdPortId(String ctdPortId) {
        this.ctdPortId = ctdPortId;
    }

    public void setCtdPortName(String ctdPortName) {
        this.ctdPortName = ctdPortName;
    }
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