package com.elemency.Midi4J.RtMidiDriver;

/**
 * brief Defined RtMidiError types. See \ref RtMidiError::Type.<br>
 * <i>native declaration : RtMidi/rtmidi_c.h:73</i><br>
 * enum values
 */
public enum RtMidiErrorType {
    /**
     * A non-critical error.<br>
     * <i>native declaration : RtMidi/rtmidi_c.h:74</i>
     */
    RTMIDI_ERROR_WARNING(0),
    /**
     * A non-critical error which might be useful for debugging.<br>
     * <i>native declaration : RtMidi/rtmidi_c.h:75</i>
     */
    RTMIDI_ERROR_DEBUG_WARNING(1),
    /**
     * The default, unspecified error type.<br>
     * <i>native declaration : RtMidi/rtmidi_c.h:76</i>
     */
    RTMIDI_ERROR_UNSPECIFIED(2),
    /**
     * No devices found on system.<br>
     * <i>native declaration : RtMidi/rtmidi_c.h:77</i>
     */
    RTMIDI_ERROR_NO_DEVICES_FOUND(3),
    /**
     * An invalid device ID was specified.<br>
     * <i>native declaration : RtMidi/rtmidi_c.h:78</i>
     */
    RTMIDI_ERROR_INVALID_DEVICE(4),
    /**
     * An error occured during memory allocation.<br>
     * <i>native declaration : RtMidi/rtmidi_c.h:79</i>
     */
    RTMIDI_ERROR_MEMORY_ERROR(5),
    /**
     * An invalid parameter was specified to a function.<br>
     * <i>native declaration : RtMidi/rtmidi_c.h:80</i>
     */
    RTMIDI_ERROR_INVALID_PARAMETER(6),
    /**
     * The function was called incorrectly.<br>
     * <i>native declaration : RtMidi/rtmidi_c.h:81</i>
     */
    RTMIDI_ERROR_INVALID_USE(7),
    /**
     * A system driver error occurred.<br>
     * <i>native declaration : RtMidi/rtmidi_c.h:82</i>
     */
    RTMIDI_ERROR_DRIVER_ERROR(8),
    /**
     * A system error occurred.<br>
     * <i>native declaration : RtMidi/rtmidi_c.h:83</i>
     */
    RTMIDI_ERROR_SYSTEM_ERROR(9),
    /**
     * A thread error occurred.<br>
     * <i>native declaration : RtMidi/rtmidi_c.h:84</i>
     */
    RTMIDI_ERROR_THREAD_ERROR(10);

    int value;

    RtMidiErrorType(int value) {
        this.value = value;
    }

    public int getIntValue() {
        return value;
    }
//    public NativeLong getNativeLongValue() { return new NativeLong((long)value); }
}
