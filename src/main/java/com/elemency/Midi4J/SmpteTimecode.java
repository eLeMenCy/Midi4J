/*
 * Copyright (C) 2020 - eLeMenCy
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

package com.elemency.Midi4J;

public class SmpteTimecode {
    private static long startTime = 0;

    /**
     * Get a milliseconds time stamp converted and formatted to SMPTE timecode.
     * @param time  time stamp to convert
     * @return      String (i.e. 03:06:40:000 - )
     */
    public static String getTimecode(double time) {

        time /= 1000;

        int hours = ((int) (time / 3600.0)) % 24;
        int minutes = ((int) (time / 60.0)) % 60;
        int seconds = ((int) time) % 60;
        int millis = ((int) (time * 1000.0)) % 1000;

        return String.format("%02d:%02d:%02d:%03d - ", hours, minutes, seconds, millis);
    }

    /**
     * Set the start time to current milliseconds.
     */
    public static void setStartTime() {
        startTime = System.currentTimeMillis();
    }

    /**
     * get the time elapsed since start time.
     * @return long
     */
    public static long getElapsedTimeSinceStartTime() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * return the start time.
     *
     * @return long
     */
    public static long getStartTime() {
        return startTime;
    }

    /**
     * Frequently used SMPTE time rates.
     */
    public enum SmpteTimecodeType {
        /**
         * 24 fps
         * 25 fps
         * 30 Drop fps
         * 30 fps
         */

        /**
         *
         */
        SMPTE_24_FPS(0),

        /**
         *
         */
        SMPTE_25_FPS(1),

        /**
         *
         */
        SMPTE_30_DROP_FPS(2),

        /**
         *
         */
        SMPTE_30_FPS(3);

        int value;

        SmpteTimecodeType(int value) {
            this.value = value;
        }

        public int getIntValue() {
            return value;
        }
    }
}
