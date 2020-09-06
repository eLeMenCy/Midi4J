package com.elemency.Midi4J.Examples;

public class App {

    public static void main(String[] args) throws Exception {

//        if (args.length > 0) {
//            "hello: " + args[0]);


        System.out.println("\n-----------------\n| Midi4J readme |\n" +
                "-----------------------------------------------------------------------------------------\n" +
                "This sample app demonstrates 3 methods on how midi IN messages can be handled in your\n" +
                "application.\n" +
                "Midi4J allows the reception, per devices, of midi IN messages, either via a centralised\n" +
                "broadcaster and/or via user implemented callback(s).\n\n" +

                "To start the sample with one or the other method, type the following arg in your gradle\n" +
                "console or in the 'Arguments' field of Intellij [run] Configurations window:\n" +
                "\trun --args=\"broadcast\" (as broadcast mode is the default, arg is not required).\n" +
                "\trun --args=\"callback\"\n" +
                "\trun --args=\"both\"\n\n" +

                "Broadcaster method (default)\n" +
                "\tHandles all midi incoming events from MidiIn sourcedevices thru a centralised\n" +
                "\tbroadcaster/listener system.\n\n" +

                "Callback method\n" +
                "\tHandles all midi incoming events thru user callbacks (one per MidiIn device)\n\n" +

                "As hinted earlier, these 2 main methods can be used together in the same app by\n" +
                "constructing MidiIn devices with the correct 'userCallback' param.\n" +
                "Set to false, the device will use broadcast mode, set to true, the opposite.\n\n" +

                "In Depth:\n" +
                "A Broadcaster, is called by native driver callback(s) automatically set within each\n" +
                "MidiIn instance at construction time. All messages are received by our app via the\n" +
                "implementation of the BroadcasterListener interface and its overriden method.\n\n" +
                "Callback(s) is/are called directly by the native driver and need to be set\n" +
                "(one per In device) in user's app.\n" +
                "-----------------------------------------------------------------------------------------");

        switch (args.length > 0 ? args[0] : "") {
            case "callback": {
                System.out.println("\n\n------------------------------");
                System.out.println("| Method with User Callbacks |");
                System.out.println("------------------------------\n");

                final WithCallbacks withCallback = new WithCallbacks();
                withCallback.init();
                break;
            }

            case "both": {
                System.out.println("\n\n--------------------");
                System.out.println("| Method with Both |");
                System.out.println("--------------------\n");

                final WithBoth withBoth = new WithBoth();
                withBoth.init();
                break;
            }

            case "seq": {
                System.out.println("\n\n--------------------");
                System.out.println("| Simple Sequencer |");
                System.out.println("--------------------\n");

                final SimpleSequencer simpleSequencer = new SimpleSequencer();
                simpleSequencer.init();
                break;
            }

            default: {
                System.out.println("\n\n---------------------------");
                System.out.println("| Method with Broadcaster |");
                System.out.println("---------------------------\n");

                final WithBroadcaster withBroadcaster = new WithBroadcaster();
                withBroadcaster.init();
                break;
            }
        }
    }
}
