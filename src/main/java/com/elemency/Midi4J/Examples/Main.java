package com.elemency.Midi4J.Examples;

public class Main {

    public static void main(String[] args) throws Exception {

//        String appType = "";
        String appType = "callback";

        switch (appType) {
            case "callback":
                final WithCallbacks awcb = new WithCallbacks();
                awcb.init();
                break;

            default:
                final WithBroadcaster awbst = new WithBroadcaster();
                awbst.init();
                break;
        }
    }

}
