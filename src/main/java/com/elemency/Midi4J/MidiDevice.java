package com.elemency.Midi4J;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MidiDevice {
    private int midiDeviceId = -1;
    private String apiName = null;
    private String portType = "Unknown";
    private int clientId = -1;
    private String clientName = "No Name";

    private int portId = -1;
    private String portName = "No Name";


    public MidiDevice() {

    }

    public MidiDevice(String fullDeviceDetails) {

        String[] params = fullDeviceDetails.split("\\|");

        // params[0] | params[1] |     params[2]   |             params[3]
        //     0     |   ALSA    |      Out/In     | Midi Through:Midi Through Port-0 14:0

        this.midiDeviceId = Integer.parseInt(params[0]);
        this.apiName = params[1];
        this.portType = params[2];

        /** Device details have different layout depending on their API - handling the difference.
         *  i.e. client ID and port ID only exist under the ALSA API.
         */
        switch (apiName) {
            case "ALSA":

                // Insert a semicolon between clientPort names and client ports ids.
                String param = params[3];
                int index = param.lastIndexOf(" ");

                fullDeviceDetails = param.substring(0, index) + ":" + param.substring(index + 1);
                params = fullDeviceDetails.split(":");// [Midi Through, Midi Through Port-0, 14, 0]

                this.clientId = Integer.parseInt(params[2]);
                this.portId = Integer.parseInt(params[3]);

                break;

            case "Jack":
                params = params[3].split(":");

                break;

            case "CoreMidi":
            case "Windows MultiMedia":
                params = new String[]{"No Name", "No Name"};

                break;

            default: // Unknown & Dummy
                return;
        }

        this.clientName = params[0];
        this.portName = params[1];

//        for (String t : params) {
//            System.out.println("test: " + t);
//        }
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

    public int getMidiDeviceId() {
        return midiDeviceId;
    }

    public String getApiName() {
        return apiName;
    }

    public int getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public int getPortId() {
        return portId;
    }

    public String getPortName() {
        return portName;
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