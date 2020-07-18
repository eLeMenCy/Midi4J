package com.elemency.Midi4J;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MidiDevice {
    private String apiName = null;

    private int midiDeviceId = -1;

    private int deviceId = -1;
    private String deviceName = "No Name";

    private boolean isConnected = false;
    private int connectedDeviceId = -1;
    private String connectedDeviceName = "No Name";

    private int connectedPortId = -1;
    private String connectedPortName = "No Name";
    
    private int portId = -1;
    private String portName = "No Name";
    private String portType = "Unknown";


    public MidiDevice() {

    }

    public MidiDevice(String fullDeviceDetails) {

        String ids = findPattern(fullDeviceDetails,"\\w+:\\w+$");
        if (!ids.equals("")){
            fullDeviceDetails = fullDeviceDetails.replace((" " + ids), "");
            fullDeviceDetails = fullDeviceDetails + "|" + ids;
        }
        fullDeviceDetails = fullDeviceDetails.replace(":", "|");

//        System.out.println("fullDeviceDetails: " + fullDeviceDetails);

        String[] params = fullDeviceDetails.split("\\|");

        // params[0] | params[1] |     params[2]   |             params[3]
        //     0     |   ALSA    |      Out/In     | Midi Through:Midi Through Port-0 14:0

        this.midiDeviceId = Integer.parseInt(params[0]);
        this.apiName = params[1];
        this.portType = params[2];
        this.deviceName = params[0];
        this.portName = params[1];

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

    public int getConnectedDeviceId() {
        return connectedDeviceId;
    }

    public String getConnectedDeviceName() {
        return connectedDeviceName;
    }

    public int getConnectedPortId() {
        return connectedPortId;
    }

    public String getConnectedPortName() {
        return connectedPortName;
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

    public void setConnectedDeviceId(int connectedDeviceId) {
        this.connectedDeviceId = connectedDeviceId;
    }

    public void setConnectedDeviceName(String connectedDeviceName) {
        this.connectedDeviceName = connectedDeviceName;
    }

    public void setConnectedPortId(int connectedPortId) {
        this.connectedPortId = connectedPortId;
    }

    public void setConnectedPortName(String connectedPortName) {
        this.connectedPortName = connectedPortName;
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