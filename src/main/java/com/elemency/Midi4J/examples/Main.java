package com.elemency.Midi4J.examples;

import com.elemency.Midi4J.examples.App2.App2;

public class Main {

    public static void main(String[] args) throws Exception {

        String appType = "";

        if ("app2".equals(appType)) {
            final App2 awbst = new App2();
            awbst.init();
        } else {
            final WithCallbacks awcb = new WithCallbacks();
            awcb.init();
        }
    }

}
