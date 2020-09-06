package com.elemency.Midi4J.Examples;

import com.elemency.Midi4J.MidiMessage;
import com.elemency.Midi4J.SmpteTimecode;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

public abstract class KeepRunning {
    private final Logger logger = LoggerFactory.getLogger(KeepRunning.class);
    protected boolean doQuit = false;
    private final boolean DISPLAY_TIMECODE = true;
    private final long DISPLAY_TIMECODE_RATE = 1000;

    protected abstract void init() throws Exception;

    public synchronized void doQuit() {
        this.doQuit = true;
    }

    public void keepRunning() throws InterruptedException {
        while (!doQuit) {
            long timeTillNextDisplayChange = DISPLAY_TIMECODE_RATE - (SmpteTimecode.getElapsedTimeSinceStartTime() % DISPLAY_TIMECODE_RATE);
            Thread.sleep(timeTillNextDisplayChange);
            if (DISPLAY_TIMECODE) {
                System.out.println(SmpteTimecode.getTimecode(SmpteTimecode.getElapsedTimeSinceStartTime()));
            }
        }
    }
}
