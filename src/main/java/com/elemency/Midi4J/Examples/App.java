package com.elemency.Midi4J.Examples;

public class App {

    public static void main(String[] args) throws Exception {

//        if (args.length > 0) {
//            "hello: " + args[0]);


        System.out.println("\n-----------------\n| Midi4J readme |\n" +
                "-----------------------------------------------------------------------------------------\n" +
                "This sample app demonstrates 2 methods on how midi IN messages can be handled.\n" +
                "Midi4J allows the reception, per devices, of midi IN messages either via a centralised\n" +
                "broadcaster or via user callbacks, the latter being called directly by the native driver\n" +
                "whilst the former is called by a callback automatically set within each MidiIn instance.\n" +
                "Look at the code and decide which method is the most adapted to your app requirements.\n\n" +

                "To start the sample with one or the other methods, type the following arg in the gradle\n" +
                "console or in the 'Arguments' field of Intellij [run] Configurations window:\n" +
                "\trun --args=\"broadcast\" (as broadcast mode is the default, arg is not required).\n" +
                "\trun --args=\"callback\"\n\n" +

                "Broadcaster method (default)\n" +
                "\tHandles all midi incoming events from MidiIn sourcedevices thru a centralised\n" +
                "\tbroadcaster/listener system.\n\n" +

                "Callback method\n" +
                "\tHandles all midi incoming events thru user callbacks\n" +
                "\t(1 callback per MidiIn source device).\n\n" +

                "These 2 methods of course can be used together in the same app by constructing MidiIn\n" +
                "devices with the correct 'userCallback' param. Set to false, the device will use\n" +
                "broadcast mode, set to true, the opposite.\n" +
                "-----------------------------------------------------------------------------------------");

        switch (args.length > 0 ? args[0] : "") {
            case "callback": {
                System.out.println("\n\n------------------------------");
                System.out.println("| Method with User Callbacks |");
                System.out.println("------------------------------\n");

                final WithCallbacks awcb = new WithCallbacks();
                awcb.init();
                break;
            }

            default: {
                System.out.println("\n\n---------------------------");
                System.out.println("| Method with Broadcaster |");
                System.out.println("---------------------------\n");

                final WithBroadcaster awbst = new WithBroadcaster();
                awbst.init();
                break;
            }
        }
    }
}
