/*
 * Copyright (C) 2020 - eLeMenCy, All Rights Reserved
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.elemency.Midi4J.Examples;

/**
 * This sample app demonstrates 3 options on how midi IN messages can be handled in your
 * application. The 4th option is a very simple monodic sequencer.<br><br>
 *
 * Midi4J allows the reception, per device, of midi IN messages, either via a centralised
 * Broadcaster/Listener and/or via user implemented Callback(s).<br><br>
 *
 * To start the sample with one or the other option, type the following arg in your gradle
 * console or in the 'Arguments' field of Intellij [run] Configurations window:<br>
 * &nbsp; &nbsp; run --args="broadcaster" (default - arg not required in that case).<br>
 * &nbsp; &nbsp; run --args="callbacks"<br>
 * &nbsp; &nbsp; run --args="both"<br>
 * &nbsp; &nbsp; run --args="sequencer"<br><br>
 *
 * Broadcaster option (default)<br>
 * Handles all midi incoming events from MidiIn source devices thru a centralised
 * broadcaster/listener system.<br><br>
 *
 * Callbacks option<br>
 * Handles all midi incoming events thru user callbacks (one per MidiIn device)<br><br>
 *
 * Both options<br>
 * As hinted earlier, the 2 main options can be used together in the same app by constructing
 * MidiIn devices with the correct user Callback param.<br>
 * Set to false, the device will use broadcast mode, set to true, the opposite.<br><br>
 *
 * In Depth:<br>
 * A Broadcaster, is called by native driver internal callback(s) automatically set within each MidiIn instance at
 * construction time, all messages are received by our app via the implementation of the BroadcasterListener interface
 * and its overridden method.<br>
 * Callback(s) is/are called directly by the native driver and need to be set (one per In device) in user's app.<br><br>
 *
 * Sequencer option<br>
 * Just a very simple and crude monodic sequencer using a timer as its engine - no MidiIn.
 */
public class App {

    /**
     * Application bootloader
     * @param args flag to switch one or the other sample.
     */
    public static void main(String[] args) {

        // Launch the correct sample (WithBroadcaster, WithCallbacks, WithBoth or SimpleSequencer)
        // based on respective args ('broadcaster', 'callbacks', 'both' or 'sequencer') passed thru args[0]
        // with 'broadcaster' being the default.
        AppOption option = AppOptionsFactory.getAppOption(args.length > 0 ? args[0] : "broadcaster");
        try {
            option.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
