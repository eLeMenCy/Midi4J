package com.elemency.Midi4J.RtMidiDriver;

import com.elemency.Midi4J.MidiDeviceMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.IntBuffer;


public class RtMidi {

    private final RtMidiLibrary lib = RtMidiLibrary.INSTANCE;
    private final Logger logger = LoggerFactory.getLogger(MidiDeviceMgr.class);

    /**
     * MIDI API specifier arguments.  See ref: RtMidi::Api.<br>
     * <i>native declaration : RtMidi/rtmidi_c.h:62</i><br>
     * enum values
     */
    public enum Api {
        /**
         * < Search for a working compiled API.<br>
         * <i>native declaration : RtMidi/rtmidi_c.h:63</i>
         * api name:         unspecified
         * api display name: Unknown
         */
        UNSPECIFIED(0),

        /**
         * < Macintosh OS-X CoreMIDI API.<br>
         * <i>native declaration : RtMidi/rtmidi_c.h:64</i>
         * api name:         core
         * api display name: CoreMidi
         */
        MACOSX_CORE(1),

        /**
         * < The Advanced Linux Sound Architecture API.<br>
         * <i>native declaration : RtMidi/rtmidi_c.h:65</i>
         * api name:         alsa
         * api display name: ALSA
         */
        LINUX_ALSA(2),

        /**
         * < The Jack Low-Latency MIDI Server API.<br>
         * <i>native declaration : RtMidi/rtmidi_c.h:66</i>
         * api name:         jack
         * api display name: JACK
         */
        UNIX_JACK(3),

        /**
         * < The Microsoft Multimedia MIDI API.<br>
         * <i>native declaration : RtMidi/rtmidi_c.h:67</i>
         * api name:         winmm
         * api display name: Windows MultiMedia
         */
        WINDOWS_MM(4),

        /**
         * < A compilable but non-functional API.<br>
         * <i>native declaration : RtMidi/rtmidi_c.h:68</i>
         * api name:         dummy
         * api display name: Dummy
         */
        RTMIDI_DUMMY(5);

        int value;

        Api(int value) {
            this.value = value;
        }

        public int getIntValue() {
            return value;
        }
    }

    /**
     *
     */
    public int getAvailableApis(IntBuffer apis, int apis_size) {
        return lib.rtmidi_get_compiled_api(apis, apis_size);
    }

    /**
     *
     */
    public String getApiName(int api) {
        return lib.rtmidi_api_name(api);
    }

    /**
     *
     */
    public String getApiLabel(int api) {
        return lib.rtmidi_api_display_name(api);
    }

    /**
     *
     */
    public int getCompiledApiByName(String name) {
        return lib.rtmidi_compiled_api_by_name(name);
    }
}
