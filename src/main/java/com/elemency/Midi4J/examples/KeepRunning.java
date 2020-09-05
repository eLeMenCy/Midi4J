package com.elemency.Midi4J.examples;

import com.elemency.Midi4J.MidiMessage;
import com.elemency.Midi4J.SmpteTimecode;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

public abstract class KeepRunning {
    private final Logger logger = LoggerFactory.getLogger(KeepRunning.class);
    protected boolean doQuit = false;
    protected Timer t = null;
    private boolean displayTimecode = true;
    private long displayTimecodeRate = 1000;

    protected abstract void init() throws Exception;

    public void processMidiInMessage(double timeStamp, MidiMessage midiMessage, Pointer userData) {
    }

    public synchronized void doQuit() {
        t.cancel();
        this.doQuit = true;
    }

    public void keepRunning() throws InterruptedException {
        while (!doQuit) {
            long timeTillNextDisplayChange = displayTimecodeRate - (SmpteTimecode.getElapsedTimeSinceStartTime() % displayTimecodeRate);
            Thread.sleep(timeTillNextDisplayChange);
            if (displayTimecode) {
                System.out.println(SmpteTimecode.getTimecode(SmpteTimecode.getElapsedTimeSinceStartTime()));
            }
        }
    }
}
