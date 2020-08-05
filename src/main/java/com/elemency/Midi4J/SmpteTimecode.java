package com.elemency.Midi4J;

public class SmpteTimecode {
    private static long startTime = 0;


    public enum SmpteTimecodeType {
    /**
     * 24 fps
     * 25 fps
     * 30 Drop fps
     * 30 fps
     */

        /**
         *
         *
         */
        SMPTE_24_FPS (0),

        /**
         *
         *
         */
        SMPTE_25_FPS (1),

        /**
         *
         *
         */
        SMPTE_30_DROP_FPS (2),

        /**
         *
         *
         */
        SMPTE_30_FPS (3);

        int value;

        SmpteTimecodeType(int value) {
            this.value = value;
        }

        public int getIntValue() { return value; }
    }

    /**
     *
     * @param time
     * @return
     */
    public static String getTimecode(double time) {

        time /= 1000;

        int hours = ((int) (time / 3600.0)) % 24;
        int minutes = ((int) (time / 60.0)) % 60;
        int seconds = ((int) time) % 60;
        int millis = ((int) (time * 1000.0)) % 1000;

        return String.format("%02d:%02d:%02d:%03d - ",hours, minutes, seconds, millis);
    }

    /**
     *
     */
    public static void setStartTime() {
        startTime = System.currentTimeMillis();
    }

    /**
     *
     * @return
     */
    public static long getElapsedTimeSinceStartTime() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     *
     * @return
     */
    public static long getStartTime() {
        return startTime;
    }
}
