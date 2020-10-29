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

import com.elemency.Midi4J.SmpteTimecode;

/**
 * Self explanatory.<br>
 * This is currently the best way I found (but likely not the most elegant) to keep the console
 * application up and running.<br>
 * - Any suggestions welcome! -
 */
public abstract class KeepRunning {
    protected boolean doQuit = false;
    protected boolean displayTimecode = true;
    private final long DISPLAY_TIMECODE_RATE = 1000;
    public synchronized void doQuit() {
        this.doQuit = true;
    }

    public void keepRunning() throws InterruptedException {

        while (!doQuit) {
            long timeTillNextDisplayChange = DISPLAY_TIMECODE_RATE - (SmpteTimecode.getElapsedTimeSinceStartTime() % DISPLAY_TIMECODE_RATE);
            Thread.sleep(timeTillNextDisplayChange);

            if (displayTimecode) {
                System.out.println(SmpteTimecode.getTimecode(SmpteTimecode.getElapsedTimeSinceStartTime()));
            }
        }
    }
}
