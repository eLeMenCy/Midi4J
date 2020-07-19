package com.elemency.Midi4J;

import com.elemency.Midi4J.RtMidiDriver.RtMidiDevice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MidiDevice {
    private RtMidiDevice rtMidiDevice = null;

    private String apiName = "Unknown";

    private int midiDeviceId = -1;

    private int deviceId = -1;
    private String deviceName = "No Name";

    private boolean isConnected = false;
    private int ctdDeviceId = -1;
    private String ctdDeviceName = "No Name";

    private int ctdPortId = -1;
    private String ctdPortName = "No Name";
    private String ctdPortType = "No Name";
    
    private int portId = -1;
    private String portName = "No Name";
    private String portType = "Unknown";


    public MidiDevice() {

    }

//    public MidiDevice(String[] fullDeviceDetails) {
    public MidiDevice(RtMidiDevice rtMidiDevice, String[] params) {
        int connectedIndex = -1;

        this.rtMidiDevice = rtMidiDevice;

//   params[0]  | params[1] |     params[2]   |     params[3]    |      params[4]       |   params[5]   | params[6]   |   params[7]   |  params[8]  |  params[9]
// -------------|-----------|-----------------|------------------|----------------------|---------------|-------------|---------------|-------------|--------------
// midiDeviceId |  apiName  |     portType    |    deviceName    |      portName        |    deviceId   |    portId   | ctdDeviceName | ctdPortType | ctdPortName
//     0        |   ALSA    |      Out/In     |   Midi Through   | Midi Through Port-0  |       14      |       0     |-->   Midi4J   |     In      |     IN
// -------------|-----------|-----------------|------------------|----------------------|---------------|-------------|---------------|-------------|--------------
// midiDeviceId |  apiName  |     portType    |    deviceName    |      portName        | ctdDeviceName | ctdPortType |  ctdPortName
//     1        |   Jack    |      Out/In     | Calf Studio Gear |     Organ MIDI In    |-->  Midi4J    |     OUT     |      Out

        midiDeviceId = Integer.parseInt(params[0]);
        apiName = params[1];
        portType = params[2];
        deviceName = params[3];
        portName = params[4];

        switch (params[1]) {
            case "ALSA":
                deviceId = Integer.parseInt(params[5]);
                portId = Integer.parseInt(params[6]);

                if (isConnected = (params.length > 7)) {
                    connectedIndex = 7;
                }

                break;
            case "Jack":
            case "CoreMidi":
            case "Windows MultiMedia":
                if (isConnected = (params.length > 5)) {
                    connectedIndex = 5;
                }
        }

        if (isConnected) {
//            connectedDeviceId = Integer.parseInt(params[0]);
            ctdDeviceName = params[connectedIndex];

//            connectedPortId = Integer.parseInt(params[8]);
            ctdPortName = params[connectedIndex + 2];
            ctdPortType = params[connectedIndex + 1];
        }

//        for (String t : params) {
//            System.out.println("test: " + t);
//        }
    }

    public String getPortType() {
        return portType;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public int getCtdDeviceId() {
        return ctdDeviceId;
    }

    public String getCtdDeviceName() {
        return ctdDeviceName;
    }

    public int getCtdPortId() {
        return ctdPortId;
    }

    public String getCtdPortName() {
        return ctdPortName;
    }

    public int getMidiDeviceId() {
        return midiDeviceId;
    }

    public String getApiName() {
        return apiName;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public int getPortId() {
        return portId;
    }

    public String getPortName() {
        return portName;
    }

    private String findPattern(String data, String regex) {

        // regex to extract ALSA client:port ids: "\\w+:\\w+$"

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        String result = "";

        if (matcher.find()) {
            result = data.substring(matcher.start(), matcher.end());
//            System.out.println("client:port -> " + result);
        }

        return result;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public void setCtdDeviceId(int ctdDeviceId) {
        this.ctdDeviceId = ctdDeviceId;
    }

    public void setCtdDeviceName(String ctdDeviceName) {
        this.ctdDeviceName = ctdDeviceName;
    }

    public void setCtdPortId(int ctdPortId) {
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