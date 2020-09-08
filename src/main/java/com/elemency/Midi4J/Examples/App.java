package com.elemency.Midi4J.Examples;

/**
 * This sample app demonstrates 3 options on how midi IN messages can be handled in your
 * application. The 4th option is a very simple monodic sequencer.<br><br>
 *
 * Midi4J allows the reception, per device, of midi IN messages, either via a centralised
 * broadcaster/Listener and/or via user implemented callback(s).<br><br>
 *
 * To start the sample with one or the other option, type the following arg in your gradle
 * console or in the 'Arguments' field of Intellij [run] Configurations window:<br>
 * &nbsp; &nbsp; run --args="broadcaster" (default - arg not required in that case).<br>
 * &nbsp; &nbsp; run --args="callbacks"<br>
 * &nbsp; &nbsp; run --args="both"<br>
 * &nbsp; &nbsp; run --args="sequencer"<br><br>
 *
 * Broadcaster option (default)<br>
 * Handles all midi incoming events from MidiIn sourcedevices thru a centralised<br>
 * broadcaster/listener system.<br><br>
 *
 * Callback option<br>
 * Handles all midi incoming events thru user callbacks (one per MidiIn device)<br><br>
 *
 * Both option<br>
 * As hinted earlier, the 2 main options can be used together in the same app by constructing MidiIn devices with the
 * correct userCallback param.<br>
 * Set to false, the device will use broadcast mode, set to true, the opposite.<br><br>
 *
 * In Depth:<br>
 * A Broadcaster, is called by native driver callback(s) automatically set within each MidiIn instance at construction
 * time, all messages are received by our app via the implementation of the BroadcasterListener interface and its
 * overridden method.<br>
 * Callback(s) is/are called directly by the native driver and need to be set (one per In device) in user's app.<br><br>
 *
 * Sequencer option<br>
 * Just a very simple and crude monodic sequencer using a timer as its engine.
 */
public class App {

    public static void main(String[] args) throws Exception {

        //A factory is used to launch the correct sample based on the option passed thru args[0]
        // with 'broadcaster' being the default.*/
        AppOption option = AppOptionsFactory.getAppOption(args.length > 0 ? args[0] : "broadcaster");
        option.init();
    }
}
