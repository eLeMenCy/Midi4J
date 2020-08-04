package com.elemency.Midi4J;

import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MidiMessage {

    private final Logger logger = LoggerFactory.getLogger(MidiMessage.class);
    private int messageSize = 0;
    private byte[] midiMessage;
    private double timeStamp = 0;


    /**
     * Creates a 3-byte short midi message.
     * @param byte0
     * @param byte1
     * @param byte2
     * @param timeStamp
     */
    public MidiMessage(int byte0, int byte1, int byte2, int timeStamp) {
        messageSize = 3;
        this.timeStamp = timeStamp;

        midiMessage = new byte[this.messageSize];
        midiMessage[0] = (byte)byte0;
        midiMessage[1] = (byte)byte1;
        midiMessage[2] = (byte)byte2;

        //TODO: Trows MidiException
        //      check that the length matches the data..
        //      jassert (byte1 >= 0xF0 || getMessageLengthFromFirstByte ((uint8) byte1) == 3);
    }

    /**
     * Creates a 2-byte short midi message.
     * @param byte0
     * @param byte1
     * @param timeStamp
     */
    public MidiMessage(int byte0, int byte1, int timeStamp) {
        messageSize = 2;
        this.timeStamp = timeStamp;

        midiMessage = new byte[this.messageSize];
        midiMessage[0] = (byte)byte0;
        midiMessage[1] = (byte)byte1;

        //TODO: Trows MidiException
        //      check that the length matches the data..
        //      jassert (byte1 >= 0xF0 || getMessageLengthFromFirstByte ((uint8) byte1) == 2);
    }

    /**
     * Creates a 1-byte short midi message.
     * @param byte0
     * @param timeStamp
     */
    public MidiMessage(int byte0, int timeStamp) {
        messageSize = 1;
        this.timeStamp = timeStamp;

        midiMessage = new byte[this.messageSize];
        midiMessage[0] = (byte)byte0;

        //TODO: Trows MidiException
        //      check that the length matches the data..
        //      jassert (byte1 >= 0xF0 || getMessageLengthFromFirstByte ((uint8) byte1) == 1);
    }

    /**
     * Creates a midi message from a block of data.
     * @param sysexData
     * @param datasize
     * @param timeStamp
     */
    public MidiMessage(byte[] sysexData, int datasize, double timeStamp) {
        messageSize = datasize;
        this.timeStamp = timeStamp;

        midiMessage = new byte[this.messageSize];
        midiMessage = sysexData;

        //TODO: Trows MidiException
        //      check that the length matches the data..
        //      jassert (byte1 >= 0xF0 || getMessageLengthFromFirstByte ((uint8) byte1) == 1);
    }

    /**
     * Creates a midi message from a native jna block of data.
     * @param message
     * @param messageSize
     * @param timeStamp
     */
    public MidiMessage(Pointer message, NativeSize messageSize, double timeStamp) {
        this.messageSize = messageSize.intValue();
        this.timeStamp = timeStamp;

        // Byte array to receive the event from native pointer.
        midiMessage = new byte[this.messageSize];
        // Read native memory data into our data byte array.
        message.read(0, midiMessage, 0, this.messageSize);
    }

    /**
     * Get the current midi message block
     * @return byte[]
     */
    public byte[] getMidiMessage() {
        return midiMessage;
    }

    /**
     * Get the current midi message block size
     * @return int
     */
    public int getMidiMessageSize() {
        return messageSize;
    }

    /**
     * Returns a human-readable description of the midi message as a string
     * @return "Note On C#3 Velocity 120 Channel 1"
     */
    public String getDescription() {

        if (isNoteOn(false)) {
            return "Note on " + getMidiNoteName(getNoteNumber(), true, true, 3) +
                    " Velocity " + getVelocity() +
                    " Channel " + getChannel();
        }

        if (isNoteOff(true)) {
            return "Note off " + getMidiNoteName(getNoteNumber(), true, true, 3) +
                    " Velocity " + getVelocity() +
                    " Channel " + getChannel();
        }

        if (isProgramChange()) {
            return "Program change " + getProgramChangeNumber() +
                    " Channel " + getChannel();
        }

        if (isPitchWheel()) {
            return "Pitch wheel " + getPitchWheelValue() +
                    " Channel " + getChannel();
        }

        if (isChannelPressure()) {
            return "Aftertouch " + getMidiNoteName(getNoteNumber(), true, true, 3) +
                    ": " + getChannelPressureValue() +
                    " Channel " + getChannel();
        }

        if (isPolyAftertouch()) {
            return "Aftertouch " + getMidiNoteName(getNoteNumber(), true, true, 3) +
                    ": " + getPolyAftertouchValue() +
                    " Channel " + getChannel();
        }

        if (isChannelPressure()) {
            return "Channel pressure " + getChannelPressureValue() +
                    " Channel " + getChannel();
        }

        if (isAllNotesOff()) {
            return "All notes off Channel " + getChannel();
        }

        if (isAllSoundOff()) {
            return "All sound off Channel " + getChannel();
        }

        if (isMetaEvent()) {
            return "Meta event";
        }

        if (isController())
        {
            String name = getControllerName (getControllerNumber());

            if (name.isEmpty())
                name = String.valueOf(getControllerNumber());

            return "Controller " + name + ": " + getControllerValue() + " Channel " + getChannel();
        }

        return toHexString();
    }

    /**
     * Returns a human-readable raw midi message as a string.
     * @return
     */
    private String toHexString() {
        String hexString = "--";
        for (int i = 0; i < messageSize; i++) {
            if (i == 0) {
                int status = midiMessage[i] & 0xFF;
                hexString += "Byte 0 = 0x" + Integer.toHexString(status) + "(" + status + "),";
            } else {
                hexString += "Byte " + i + " = " + midiMessage[i] + ",";
            }
        }
        return hexString;
    }

    /** Returns the name of a midi note number.
     * E.g "C", "D#", etc.
     * @param noteNumber           the midi note number, 0 to 127
     * @param useSharps            if true, sharpened notes are used, e.g. "C#", otherwise they'll be flattened, e.g. "Db"
     * @param includeOctaveNumber  if true, the octave number will be appended to the string, e.g. "C#4"
     * @param octaveNumForMiddleC  if an octave number is being appended, this indicates the number that will be used for middle C's octave
     * @link getMidiNoteInHertz
     */
    public static String getMidiNoteName(int noteNumber, boolean useSharps, boolean includeOctaveNumber, int octaveNumForMiddleC) {
        String[] sharpNoteNames = {"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};
        String[] flatNoteNames = {"C","Db","D","Eb","E","F","Gb","G","Ab","A","Bb","B"};

        if (noteNumber > 0 && noteNumber < 128) {
            String s = (useSharps ? sharpNoteNames[noteNumber % 12] : flatNoteNames[noteNumber % 12]);
            if (includeOctaveNumber) {
                s += (noteNumber / 12 + (octaveNumForMiddleC - 5));
            }

            return s;
        }

        return "--";
    }

    /**
     * Returns this message's timestamp.
     *
     * @return
     */
    public double getTimeStamp() {
        return timeStamp;
    }

    /**
     * Changes the message's associated timestamp.
     *
     * @param newTimestamp
     */
    public void setTimeStamp(double newTimestamp) {
        this.timeStamp = newTimestamp;
    }

    /**
     * Adds a value to the message's timestamp.
     */
    public void addToTimeStamp(double delta) {
        this.timeStamp += delta;
    }

    /**
     * Return a copy of this message with a new timestamp.
     *
     * @param newTimestamp
     * @return
     */
    public MidiMessage withTimeStamp(double newTimestamp) {

        return null;
    }

    /**
     * Returns current message's midi channel.
     */
    public int getChannel() {

        if ((midiMessage[0] & 0xF0) == 0xF0) {
            return 0;
        }

        return (midiMessage[0] & 0xF) + 1;
    }

    /**
     * Changes the message's midi channel.
     *
     * @param number
     */
    public void setChannel(int number) {
        if (number < 1 || number > 16) {
            //TODO: throw exception ?
            logger.warn("A Midi voice channel can only be between 1 and 16");
            return;
        }

        midiMessage[0] = (byte) ((midiMessage[0] & 0xF0) | ((number - 1) & 0xF));
    }

    /**
     * Returns true if the message applies to the given midi channel.
     *
     * @param number
     * @return
     */
    public boolean isForChannel(int number) {

        return (midiMessage[0] & 0xF) + 1 == (number & 0xF);
    }

    /** Creates a system-exclusive message.
     The data passed in is wrapped with header and tail bytes of 0xF0 and 0xF7.
     */
    public static MidiMessage createSysExMessage (byte[] sysexData, int dataSize) {
        byte[] result = new byte[dataSize + 2];

        result[0] = (byte)0xF0;

        for (int i = 0; i < dataSize; i ++) {
            result[i + 1] = sysexData[i];
        }
        result[dataSize + 1] = (byte)0xF7;

        return new MidiMessage(result, dataSize + 2, 0);
    }

    /**
     * Returns true if this is a system-exclusive message.
     *
     * @return
     */
    public boolean isSysEx() {
        return (midiMessage[0] & 0xF0) == 0xF0;
    }

    /**
     * Returns a byte array of sysex data inside the message.
     *
     * @return
     */
    public byte[] getSysExData() {

        return isSysEx() ? midiMessage : null;
    }

    /**
     * Returns the size of the sysex data.
     *
     * @return
     */
    public int getSysExDataSize() {
        return isSysEx() ? messageSize - 2 : 0;
    }

    /**
     *
     * @param command
     * @param channel
     * @return
     */
    private static int statusByte(int command, int channel) {
        return ((command & 0xF0) | ((channel - 1) & 0x0F));
    }

    /**
     * Returns true if this message is a 'key-down' event.
     *
     * @param returnTrueForVelocity0
     * @return
     */
    public boolean isNoteOn(boolean returnTrueForVelocity0) {

        return ((midiMessage[0] & 0xF0) == 0x90) && (returnTrueForVelocity0 || midiMessage[2] != 0);

    }

    /** Creates a key-down message (using an integer velocity).
     * @param channel      the midi channel, in the range 1 to 16
     * @param noteNumber   the key number, 0 to 127
     * @param velocity     in the range 0 to 127
     * see isNoteOn
     */
    public static MidiMessage noteOn (int channel, int noteNumber, int velocity) {
        return new MidiMessage(statusByte(0x90, channel), (noteNumber & 127), velocity, 0);
    }

    /**
     * Creates a key-down message (using a floating-point velocity).
     * @param channel      the midi channel, in the range 1 to 16
     * @param noteNumber   the key number, 0 to 127
     * @param velocity     in the range 0 to 1.0
     * see isNoteOn
     */
    public static MidiMessage noteOn (int channel, int noteNumber, float velocity) {
        return noteOn(channel, noteNumber, (int)(127.0f * velocity));
    }

    /** Creates a key-up message.
     * @param channel      the midi channel, in the range 1 to 16
     * @param noteNumber   the key number, 0 to 127
     * @param velocity     in the range 0 to 127
     * @link isNoteOff
     */
    public static MidiMessage noteOff (int channel, int noteNumber, int velocity) {
        return new MidiMessage(statusByte(0x80, channel), noteNumber, velocity, 0);
    }


    /** Creates a key-up message.
     * @param channel      the midi channel, in the range 1 to 16
     * @param noteNumber   the key number, 0 to 127
     * @param velocity     in the range 0 to 1.0
     * @link isNoteOff
     */
    public static MidiMessage noteOff (int channel, int noteNumber, float velocity) {
        return noteOff(channel, noteNumber, (int)(127.0f * velocity));
    }

    /** Creates a key-up message.@param channel
     * the midi channel, in the range 1 to 16
     * @param noteNumber   the key number, 0 to 127
     * @link isNoteOff
     */
    public static MidiMessage noteOff (int channel, int noteNumber) {
        return noteOff(channel, noteNumber, 0);
    }

    /**
     * Returns true if this message is a 'key-up' event.
     *
     * @param returnTrueForNoteOnVelocity0
     * @return
     */
    public boolean isNoteOff(boolean returnTrueForNoteOnVelocity0) {
        return ((midiMessage[0] & 0xF0) == 0x80)
                || (midiMessage.length == 3)
                && (returnTrueForNoteOnVelocity0
                && (midiMessage[2] == 0)
                && ((midiMessage[0] & 0xF0) == 0x90));
    }

    /**
     * Returns true if this message is a 'key-down' or 'key-up' event.
     *
     * @return
     */
    public boolean isNoteOnOrOff() {
        return ((midiMessage[0] & 0xF0) == 0x90 || (midiMessage[0] & 0xF0) == 0x80);
    }

    /**
     * Returns the midi note number for note-on and note-off messages.
     *
     * @return
     */
    public int getNoteNumber() {

        return midiMessage[1];
    }

    /**
     * Changes the midi note number of a note-on or note-off message.
     *
     * @param newNoteNumber
     */
    public void setNoteNumber(byte newNoteNumber) {

        if (isNoteOnOrOff() || isPolyAftertouch() || isChannelPressure())
            midiMessage[1] = newNoteNumber;
    }

    /**
     * Returns the velocity of a note-on or note-off message.
     *
     * @return
     */
    public int getVelocity() {

        if (isNoteOnOrOff())
            return midiMessage[2];
        return 0;
    }

    /**
     * Changes the velocity of a note-on or note-off message.
     *
     * @param newVelocity
     */
    public void setVelocity(float newVelocity) {
        if (isNoteOnOrOff()) {
            midiMessage[2] = (byte) (127.0f * newVelocity);
        }
    }

    /**
     * Returns the velocity of a note-on or note-off message.
     *
     * @return
     */
    public float getFloatVelocity() {
        return getVelocity() * (1.0f / 127.0f);
    }

    /**
     * Multiplies the velocity of a note-on or note-off message by a given amount.
     *
     * @param scaleFactor
     */
    public void multiplyVelocity(float scaleFactor) {
        setVelocity(getFloatVelocity() * scaleFactor);
    }

    /**
     * Returns true if this message is a 'sustain pedal down' controller message.
     *
     * @return
     */
    public boolean isSustainPedalOn() {
        return isControllerOfType(0x40) && midiMessage[2] >= 64;
    }

    /**
     * Returns true if this message is a 'sustain pedal up' controller message.
     *
     * @return
     */
    public boolean isSustainPedalOff() {
        return isControllerOfType(0x40) && midiMessage[2] < 64;
    }

    /**
     * Returns true if this message is a 'sostenuto pedal down' controller message.
     *
     * @return
     */
    public boolean isSostenutoPedalOn() {
        return isControllerOfType(0x42) && midiMessage[2] >= 64;
    }

    /**
     * Returns true if this message is a 'sostenuto pedal up' controller message.
     *
     * @return
     */
    public boolean isSostenutoPedalOff() {
        return isControllerOfType(0x42) && midiMessage[2] < 64;
    }

    /**
     * Returns true if this message is a 'soft pedal down' controller message.
     *
     * @return
     */
    public boolean isSoftPedalOn() {
        return isControllerOfType(0x43) && midiMessage[2] >= 64;
    }

    /**
     * Returns true if this message is a 'soft pedal up' controller message.
     *
     * @return
     */
    public boolean isSoftPedalOff() {
        return isControllerOfType(0x43) && midiMessage[2] < 64;
    }

    /** Creates a program-change message.
     @param channel          the midi channel, in the range 1 to 16
     @param programNumber    the midi program number, 0 to 127
     @link isProgramChange, getGMInstrumentName
     */
    static MidiMessage programChange (int channel, int programNumber) {
        return new MidiMessage(statusByte(0xC0, channel), programNumber & 0x7F);
    }

    /**
     * Returns true if the message is a program (patch) change message.
     *
     * @return
     */
    public boolean isProgramChange() {
        return (midiMessage[0] & 0xF0) == 0xC0;
    }

    /**
     * Returns the new program number of a program change message.
     *
     * @return
     */
    public int getProgramChangeNumber() {

        return midiMessage[1];
    }

    /** Creates a pitch-wheel move message.
     * @param channel      the midi channel, in the range 1 to 16
     * @param position     the wheel position, in the range 0 to 16383
     * @link isPitchWheel
     */
    static MidiMessage pitchWheel (int channel, int position) {
        return new MidiMessage (statusByte(0xE0, channel), position & 127, (position >> 7) & 127, 0);
    }

    /**
     * Returns true if the message is a pitch-wheel move.
     *
     * @return
     */
    public boolean isPitchWheel() {
        return (midiMessage[0] & 0xF0) == 0xE0;
    }

    /**
     * Returns the pitch wheel position from a pitch-wheel move message.
     *
     * @return
     */
    public int getPitchWheelValue() {
        return midiMessage[1] | midiMessage[2] << 7;
    }

    /** Creates a channel-pressure change event.
     * @param channel              the midi channel: 1 to 16
     * @param pressure             the pressure, 0 to 127
     * @link isChannelPressure
     */
    static MidiMessage channelPressureChange (int channel, int pressure) {
        return new MidiMessage (statusByte(0xD0, channel), pressure & 0x7F, 0);
    }

    /**
     * Returns true if the message is a channel-pressure change event.
     *
     * @return
     */
    public boolean isChannelPressure() {

        return ((midiMessage[0] & 0xF0) == 0xD0);
    }

    /**
     * Returns the pressure from a channel pressure change message.
     *
     * @return
     */
    public int getChannelPressureValue() {
        if (isChannelPressure()) {
            return midiMessage[1];
        }
        return 0;
    }

    /** Creates an aftertouch message.
     * @param channel              the midi channel, in the range 1 to 16
     * @param noteNumber           the key number, 0 to 127
     * @param aftertouchAmount     the amount of aftertouch, 0 to 127
     * @link isAftertouch
     */
    static MidiMessage aftertouchChange (int channel, int noteNumber, int aftertouchAmount) {
        return new MidiMessage (statusByte(0xA0, channel), noteNumber & 0x7F, aftertouchAmount & 0x7F);
    }


    /**
     * Returns true if the message is a Polyphonic Aftertouch event.
     *
     * @return
     */
    public boolean isPolyAftertouch() {

        return ((midiMessage[0] & 0xF0) == 0xA0);
    }

    /**
     * Returns the amount of Poliphonic Aftertouch from anAftertouch messages.
     *
     * @return
     */
    public int getPolyAftertouchValue() {
        if (isPolyAftertouch()) {
            return midiMessage[2];
        }
        return 0;
    }

    /**
     * Returns true if this is a midi controller message.
     *
     * @return
     */
    public boolean isController() {
        return ((midiMessage[0] & 0xF0) == 0xB0);
    }

    /**
     *
     * @param controllerNumber
     * @return
     */
    private String getControllerName(int controllerNumber) {
        
        String ctrlNames[] = {
            "Bank Select","Modulation Wheel (coarse)","Breath controller (coarse)",
            "--",
            "Foot Pedal (coarse)","Portamento Time (coarse)","Data Entry (coarse)",
            "Volume (coarse)","Balance (coarse)",
            "--",
            "Pan position (coarse)","Expression (coarse)","Effect Control 1 (coarse)",
            "Effect Control 2 (coarse)",
            "--", "--",
            "General Purpose Slider 1","General Purpose Slider 2",
            "General Purpose Slider 3","General Purpose Slider 4",
            "--", "--", "--", "--", "--", "--", "--", "--", "--", "--", "--", "--",
            "Bank Select (fine)","Modulation Wheel (fine)","Breath controller (fine)",
            "--",
            "Foot Pedal (fine)","Portamento Time (fine)","Data Entry (fine)","Volume (fine)",
            "Balance (fine)", "--","Pan position (fine)","Expression (fine)",
            "Effect Control 1 (fine)","Effect Control 2 (fine)",
            "--", "--", "--", "--", "--", "--", "--", "--", "--",
            "--", "--", "--", "--", "--", "--", "--", "--", "--",
            "Hold Pedal (on/off)","Portamento (on/off)","Sustenuto Pedal (on/off)","Soft Pedal (on/off)",
            "Legato Pedal (on/off)","Hold 2 Pedal (on/off)","Sound Variation","Sound Timbre",
            "Sound Release Time","Sound Attack Time","Sound Brightness","Sound Control 6",
            "Sound Control 7","Sound Control 8","Sound Control 9","Sound Control 10",
            "General Purpose Button 1 (on/off)","General Purpose Button 2 (on/off)",
            "General Purpose Button 3 (on/off)","General Purpose Button 4 (on/off)",
            "--", "--", "--", "--", "--", "--", "--",
            "Reverb Level","Tremolo Level","Chorus Level","Celeste Level",
            "Phaser Level","Data Button increment","Data Button decrement","Non-registered Parameter (fine)",
            "Non-registered Parameter (coarse)","Registered Parameter (fine)","Registered Parameter (coarse)",
            "--", "--", "--", "--", "--", "--", "--", "--", "--",
            "--", "--", "--", "--", "--", "--", "--", "--", "--",
            "All Sound Off","All Controllers Off","Local Keyboard (on/off)","All Notes Off",
            "Omni Mode Off","Omni Mode On","Mono Operation","Poly Operation"
        };

        return ctrlNames[controllerNumber];
    }

    /**
     * Returns the controller number of a controller message.
     *
     * @return
     */
    public int getControllerNumber() {
        return midiMessage[1];
    }

    /**
     * Returns the controller value from a controller message.
     *
     * @return
     */
    public int getControllerValue() {
        return midiMessage[2];
    }

    /**
     * Returns true if this message is a controller message and if it has the specified controller type.
     *
     * @param controllerType
     * @return
     */
    public boolean isControllerOfType(int controllerType) {
        return ((midiMessage[0] & 0xF0) == 0xB0) && (midiMessage[1] == controllerType);
    }

    /** Creates a controller message.
     * @param channel          the midi channel, in the range 1 to 16
     * @param controllerType   the type of controller
     * @param value            the controller value
     * @link isController
     */
    static MidiMessage controllerEvent (int channel, int controllerType, int value) {
        return new MidiMessage(statusByte(0xB0, channel), (controllerType & 127), (value & 127),0);
    }


    /** Creates an all-notes-off message.
     * @param channel              the midi channel, in the range 1 to 16
     * @link isAllNotesOff
     */
    public static MidiMessage allNotesOff (int channel) {
        return controllerEvent(channel, 123, 0);
    }

    /**
     * Checks whether this message is an all-notes-off message.
     *
     * @return
     */
    public boolean isAllNotesOff() {
        return ((midiMessage[0] & 0xF0) == 0xB0) && (midiMessage[1] == 123) ;
    }

    /** Creates an all-sound-off message.
     * @param channel              the midi channel, in the range 1 to 16
     * @link isAllSoundOff
     */
    public static MidiMessage allSoundOff (int channel) {
        return controllerEvent(channel, 120, 0);
    }

    /**
     * Checks whether this message is an all-sound-off message.
     *
     * @return
     */
    public boolean isAllSoundOff() {
        return (midiMessage[1] == 120) && ((midiMessage[0] & 0xF0) == 0xB0);
    }

    /** Creates an all-controllers-off message.
     * @param channel              the midi channel, in the range 1 to 16
     * @link isResetAllControllers
     */
    public static MidiMessage allControllersOff (int channel) {
        return controllerEvent(channel, 121, 0);
    }

    /**
     * Checks whether this message is a reset all controllers message.
     *
     * @return
     */
    public boolean isResetAllControllers() {
        return ((midiMessage[0] & 0xF0) == 0xB0) && (midiMessage[1] == 121) ;
    }

    /**
     *
     * @return
     */
    public String timeStampToTimecode() {

        double time = timeStamp;

        int hours = ((int) (time / 3600.0)) % 24;
        int minutes = ((int) (time / 60.0)) % 60;
        int seconds = ((int) time) % 60;
        int millis = ((int) (time * 1000.0)) % 1000;

        return hours + ":" + minutes + ":" + seconds + ":" + millis + " - ";

    }

    /**
     * Returns true if this event is a meta-event.
     *
     * @return
     */
    public boolean isMetaEvent() {
        return (midiMessage[0] & 0xFF) == 0xFF;
    }

    //TODO: ??
    /**
     * Returns true if this is an active-sense message.
     *
     * @return
     * /
    public boolean isActiveSense() {
        return (midiMessage[0] & 0xFE) == 0xFE;
    }

    /**
     * Returns a meta-event's type number.
     *
     * @return
     * /
    public int getMetaEventType() {
        return (midiMessage[0] & 0xFF) != 0xFF ? -1 : midiMessage[1];
    }

    /**
     * Returns a pointer to the data in a meta-event.
     * /
    public Pointer getMetaEventData() {
        return null;
    }

    /**
     * Returns the length of the data for a meta-event.
     *
     * @return
     * /
    public int getMetaEventLength() {
        return 0;
    }

    /**
     * Returns true if this is a 'track' meta-event.
     *
     * @return
     * /
    public boolean isTrackMetaEvent() {
        return false;
    }

    /** Creates an end-of-track meta-event.
     *  @link isEndOfTrackMetaEvent
     * /
    public static MidiMessage endOfTrack() {
        return null;
    }

    /**
    * Returns true if this is an 'end-of-track' meta-event.
    *
    * @return
    * /
   public boolean isEndOfTrackMetaEvent() {
       return false;
   }

   /**
    * Returns true if this is an 'track name' meta-event.
    *
    * @return
    * /
   public boolean isTrackNameEvent() {
       return false;
   }

   /**
    * Returns true if this is a 'text' meta-event.
    *
    * @return
    * /
   public boolean isTextMetaEvent() {
       return false;
   }

   /**
    * Returns the text from a text meta-event.
    *
    * @return
    * /
   public String getTextFromTextMetaEvent() {
       return "0";
   }

   /**
    * Returns true if this is a 'tempo' meta-event.
    *
    * @return
    * /
   public boolean isTempoMetaEvent() {
       return false;
   }

   /**
    * Returns the tick length from a tempo meta-event.
    *
    * @param timeFormat
    * @return
    * /
   public double getTempoMetaEventTickLength(short timeFormat) {
       return 0;
   }

   /**
    * Calculates the seconds-per-quarter-note from a tempo meta-event.
    *
    * @return
    * /
   public double getTempoSecondsPerQuarterNote() {
       return 0;
   }

   /**
    * Returns true if this is a 'time-signature' meta-event.
    *
    * @return
    * /
   public boolean isTimeSignatureMetaEvent() {
       return false;
   }

   /**
    * Returns the time-signature values from a time-signature meta-event.
    * /
   public void getTimeSignatureInfo(int numerator, int denominator) {

   }

   /**
    * Returns true if this is a 'key-signature' meta-event.
    *
    * @return
    * /
   public boolean isKeySignatureMetaEvent() {
       return false;
   }

   /**
    * Returns the key from a key-signature meta-event.
    *
    * @return
    * /
   public int getKeySignatureNumberOfSharpsOrFlats() {
       return 0;
   }

   /**
    * Returns true if this key-signature event is major, or false if it's minor.
    *
    * @return
    * /
   public boolean isKeySignatureMajorKey() {
       return false;
   }

   /**
    * Returns true if this is a 'channel' meta-event.
    *
    * @return
    * /
   public boolean isMidiChannelMetaEvent() {
       return false;
   }

   /**
    * Returns the channel number from a channel meta-event.
    *
    * @return
    * /
   public int getMidiChannelMetaEventChannel() {
       return 0;
   }

   /**
    * Returns true if this is a midi start event.
    *
    * @return
    * /
   public boolean isMidiStart() {
       return false;
   }

   /**
    * Returns true if this is a midi continue event.
    *
    * @return
    * /
   public boolean isMidiContinue() {
       return false;
   }

   /**
    * Returns true if this is a midi stop event.
    *
    * @return
    * /
   public boolean isMidiStop() {
       return false;
   }

   /**
    * Returns true if this is a midi clock event.
    *
    * @return
    * /
   public boolean isMidiClock() {
       return false;
   }

   /**
    * Returns true if this is a song-position-pointer message.
    *
    * @return
    * /
   public boolean isSongPositionPointer() {
       return false;
   }

   /**
    * Returns the midi beat-number of a song-position-pointer message.
    *
    * @return
    * /
   public int getSongPositionPointerMidiBeat() {
       return 0;
   }

   /**
    * Returns true if this is a quarter-frame midi timecode message.
    *
    * @return
    * /
   public boolean isQuarterFrame() {
       return false;
   }

   /**
    * Returns the sequence number of a quarter-frame midi timecode message.
    *
    * @return
    * /
   public int getQuarterFrameSequenceNumber() {
       return 0;
   }

   /**
    * Returns the value from a quarter-frame message.
    *
    * @return
    * /
   public int getQuarterFrameValue() {
       return 0;
   }

   /**
    * Returns true if this is a full-frame midi timecode message.
    *
    * @return
    * /
   public boolean isFullFrame() {
       return false;
   }

   /**
    * Extracts the timecode information from a full-frame midi timecode message.
    * /
   public void getFullFrameParameters(int hours, int minutes, int seconds, int frames, SmpteTimecodeType timecodeType) {

   }
*/

}
        
