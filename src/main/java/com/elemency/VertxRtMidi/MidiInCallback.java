package com.elemency.VertxRtMidi;

import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.sun.jna.Callback;
import com.sun.jna.Pointer;

/** The type of a RtMidi callback function.
 * See ref RtMidiIn::RtMidiCallback.
 * <i>native declaration : /run/media/elemency/Data/Prjs/SandBox/Midi/RtMidi/rtmidi_c.h</i>
 */
public interface MidiInCallback extends Callback {
    /**
     * @param timeStamp     The time at which the message has been received.
     * @param message       The midi message.
     * @param messageSize   Size of the Midi message.
     * @param userData      Additional user data for the callback.
     */
    void process(double timeStamp, Pointer message, NativeSize messageSize, Pointer userData);
}
