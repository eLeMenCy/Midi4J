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

import com.elemency.Midi4J.RtMidiDriver.RtMidiLibrary;
import com.sun.jna.NativeLibrary;

/**
 * This sample app demonstrates 4 options on how midi IN messages can be handled in your
 * application. The 5th option is a very simple monodic sequencer.<br><br>
 *
 * Midi4J allows the reception, per device, of midi IN messages, either via a centralised
 * Broadcaster/Listener, a user implemented Callback(s) or via polling (Loop).<br><br>
 *
 * To start the sample with one or the other option, type the following arg in your gradle
 * console or in the 'Arguments' field of Intellij [run] Configurations window:<br>
 * &nbsp; &nbsp; run --args="broadcaster" (default - arg not required in that case).<br>
 * &nbsp; &nbsp; run --args="callbacks"<br>
 * &nbsp; &nbsp; run --args="both"<br>
 * &nbsp; &nbsp; run --args="loop"<br>
 * &nbsp; &nbsp; run --args="sequencer"<br><br>
 *
 * <b>Broadcaster option</b> (default)<br>
 * Handles all midi incoming events from MidiIn source devices thru a centralised
 * broadcaster/listener system.<br>
 * When a midi message is received from the native driver (via the automatically set MidiIn instance internal callback),<br>
 * it is forwarded directly to an internal Broadcaster which itself publishes it to a registered listener (one for all In source devices) in user's app.<br><br>
 *
 * <b>Callbacks option</b><br>
 * Handles all midi incoming events thru user callbacks (one per MidiIn device)<br>
 * User Callback(s) is/are called directly by the native driver and need to be created (one per In source device) by the user in their app.<br><br>
 *
 * <b>Both options</b><br>
 * The 2 main options can be used together in the same app by constructing
 * MidiIn devices with the correct user Callback flag.<br>
 * Set to false, the device will use broadcast mode (default), set to true, the opposite.<br><br>
 *
 * <b>Loop option</b><br>
 * Handles all incoming midi events via polling (i.e. thru a game loop).
 * Although I didn't try, it should also work in addition to callback(s) and/or a broadcaster.<br><br>
 *
 * <b>Sequencer option</b><br>
 * Just a very simple and crude monodic sequencer using a timer as its engine - no MidiIn.
 */
public class App {

    /**
     * Application bootloader
     * @param args flag to switch one or the other sample.
     */
    public static void main(String[] args) {

        // Launch the correct sample (WithBroadcaster, WithCallbacks, WithBoth, WithLoop or SimpleSequencer)
        // based on respective args ('broadcaster', 'callbacks', 'both', 'loop' or 'sequencer') passed thru args[0]
        // with 'broadcaster' being the default.
        try {
            AppOption option = AppOptionsFactory.getAppOption(args.length > 0 ? args[0] : "broadcaster");
            option.init();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
