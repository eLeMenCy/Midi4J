package com.elemency.VertxRtMidi.RtMidiLib;

import com.sun.jna.NativeLong;

/**
 * MIDI API specifier arguments.  See ref: RtMidi::Api.<br>
 * <i>native declaration : /run/media/elemency/Data/Prjs/SandBox/Midi/tMidi/rtmidi_c.h:62</i><br>
 * enum values
 */
public enum RtMidiApi {
    /**
     * < Search for a working compiled API.<br>
     * <i>native declaration : /run/media/elemency/Data/Prjs/SandBox/Midi/MidiDevice/rtmidi_c.h:63</i>
     */
    RTMIDI_API_UNSPECIFIED (0),
    /**
     * < Macintosh OS-X CoreMIDI API.<br>
     * <i>native declaration : /run/media/elemency/Data/Prjs/SandBox/Midi/MidiDevice/rtmidi_c.h:64</i>
     */
    RTMIDI_API_MACOSX_CORE (1),
    /**
     * < The Advanced Linux Sound Architecture API.<br>
     * <i>native declaration : /run/media/elemency/Data/Prjs/SandBox/Midi/MidiDevice/rtmidi_c.h:65</i>
     */
    RTMIDI_API_LINUX_ALSA (2),
    /**
     * < The Jack Low-Latency MIDI Server API.<br>
     * <i>native declaration : /run/media/elemency/Data/Prjs/SandBox/Midi/MidiDevice/rtmidi_c.h:66</i>
     */
    RTMIDI_API_UNIX_JACK (3),
    /**
     * < The Microsoft Multimedia MIDI API.<br>
     * <i>native declaration : /run/media/elemency/Data/Prjs/SandBox/Midi/MidiDevice/rtmidi_c.h:67</i>
     */
    RTMIDI_API_WINDOWS_MM (4),
    /**
     * < A compilable but non-functional API.<br>
     * <i>native declaration : /run/media/elemency/Data/Prjs/SandBox/Midi/MidiDevice/rtmidi_c.h:68</i>
     */
    RTMIDI_API_RTMIDI_DUMMY (5),
    /**
     * < Number of values in this enum.<br>
     * <i>native declaration : /run/media/elemency/Data/Prjs/SandBox/Midi/MidiDevice/rtmidi_c.h:69</i>
     */
    RTMIDI_API_NUM (6);

    int value;

    RtMidiApi(int value) {
        this.value = value;
    }

    public int getIntValue() { return value; }
//    public NativeLong getNativeLongValue() { return new NativeLong((long)value); }
}
