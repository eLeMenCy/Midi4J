package com.elemency.Midi4J.examples;

import com.elemency.Midi4J.MidiMessage;
import com.elemency.Midi4J.SmpteTimecode;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class KeepAppRunning {
    private final Logger logger = LoggerFactory.getLogger(KeepAppRunning.class);
    protected boolean doQuit = false;

    protected abstract void Init() throws Exception;

    public void processMidiInMessage(double timeStamp, MidiMessage midiMessage, Pointer userData) {}


    public synchronized void doQuit() {
        this.doQuit = true;
    }

    public void keepRunning() throws InterruptedException {
        while (!this.doQuit) {
            long timeTillNextDisplayChange = 1000 - (SmpteTimecode.getElapsedTimeSinceStartTime() % 1000);

            Thread.sleep(timeTillNextDisplayChange);

            System.out.println(SmpteTimecode.getTimecode(SmpteTimecode.getElapsedTimeSinceStartTime()));

            if (!doQuit) {
//                logger.debug("heartbeat...");
            }
        }
    }
}
