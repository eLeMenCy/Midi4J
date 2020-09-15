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
 *
 * This file incorporates work derived and translated to Java from the
 *      C++ juce_MidiMessage class part of the JUCE library and is covered
 *      by the following copyright and permission notice:
 *
 *      Copyright (c) 2020 - Raw Material Software Limited
 *
 *      The incorporated code, part of this file, is provided under the terms of
 *      the ISC license
 *      http://www.isc.org/downloads/software-support-policy/isc-license
 *      Permission to use, copy, modify, and/or distribute this software for any
 *      purpose with or without fee is hereby granted provided that the above
 *      copyright notice and this permission notice appear in all copies.
 *
 *      JUCE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY, AND ALL WARRANTIES, WHETHER
 *      EXPRESSED OR IMPLIED, INCLUDING MERCHANTABILITY AND FITNESS FOR PURPOSE, ARE
 *      DISCLAIMED.
 */

package com.elemency.Midi4J;

import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.sun.jna.Pointer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 *
 * A Utility class to handle Midi messages.
 *
 * Interesting link(s)
 * https://www.nyu.edu/classes/bello/FMT_files/9_MIDI_code.pdf
 */
public class MidiMessage implements Cloneable {

    private final Logger logger = LoggerFactory.getLogger(MidiMessage.class);
    private int midiDataSize = 0;
    private byte[] midiData;
    private double timeStamp = 0;

    /**
     * Creates a 3-byte short midi message.
     *
     * @param byte0 Status byte (Status + Channel)
     * @param byte1 data1 byte
     * @param byte2 data2 byte
     * @param timeStamp time stamp
     */
    public MidiMessage(int byte0, int byte1, int byte2, double timeStamp) {
        midiDataSize = 3;
        this.timeStamp = timeStamp;

        if (getMessageLength(byte0) != 3) {
            throw new MidiException("Status byte provided doesn't correspond to a 3 bytes midi message" + Integer.toHexString(byte0).toUpperCase());
        }

        if (byte0 >= 0xF0) {
            throw new MidiException("The Status of a 3 bytes message should be < 0xF0: 0x" + Integer.toHexString(byte0).toUpperCase());
        }

        midiData = new byte[midiDataSize];
        midiData[0] = (byte) byte0;
        midiData[1] = (byte) byte1;
        midiData[2] = (byte) byte2;
    }

    /**
     * Creates a 2-byte short midi message.
     *
     * @param byte0 Status byte (Status + Channel)
     * @param byte1 data1 byte
     * @param timeStamp time stamp
     */
    public MidiMessage(int byte0, int byte1, double timeStamp) {
        midiDataSize = 2;
        this.timeStamp = timeStamp;

        if (getMessageLength(byte0) != 2) {
            throw new MidiException("Status byte provided doesn't correspond to a 2 bytes midi message " + Integer.toHexString(byte0).toUpperCase());
        }

        if (byte0 >= 0xF0) {
            throw new MidiException("The Status of a 2 bytes message should be < 0xF0: 0x" + Integer.toHexString(byte0).toUpperCase());
        }

        midiData = new byte[this.midiDataSize];
        midiData[0] = (byte) byte0;
        midiData[1] = (byte) byte1;
    }

    /**
     * Creates a 1-byte short midi message.
     *
     * @param byte0 Status byte (Status + Channel)
     * @param timeStamp time stamp
     */
    public MidiMessage(int byte0, double timeStamp) {
        midiDataSize = 1;
        this.timeStamp = timeStamp;

        if (getMessageLength(byte0) != 1) {
            throw new MidiException("Status byte provided doesn't correspond to a 1 byte midi message" + Integer.toHexString(byte0).toUpperCase());
        }

        midiData = new byte[this.midiDataSize];
        midiData[0] = (byte) byte0;
    }

    /**
     * Creates a midi message from a block of data.
     *
     * @param midiData  midi message set in a byte[]
     * @param datasize  midi message size
     * @param timeStamp time stamp
     */
    public MidiMessage(byte[] midiData, int datasize, double timeStamp) {
        midiDataSize = datasize;
        this.timeStamp = timeStamp;

//        this.midiDataSize = 0;
//        midiData = null;

        if (midiDataSize < 1) {
            throw new MidiException("A multibyte message size should be > 0");
        }

        this.midiData = new byte[this.midiDataSize];
        this.midiData = midiData;
    }

    /***
     * Creates a midi message from a native jna block of data.
     *
     * @param midiData      midi message set in a jna pointer
     * @param midiDataSize  midi message size (jnaerator native)
     * @param timeStamp     time stamp
     */
    public MidiMessage(@NotNull Pointer midiData,
                       @NotNull NativeSize midiDataSize,
                       double timeStamp)
    {
        this.midiDataSize = midiDataSize.intValue();
        this.timeStamp = timeStamp;

        if (midiData == null) {
            throw new NullPointerException("A native Midi Message can't be null.");
        }

        if (this.midiDataSize < 1) {
            throw new MidiException("A native Midi Message size should be > 0.");
        }

        // Byte array to receive the event from native pointer.
        this.midiData = new byte[this.midiDataSize];

        // Read native memory data into our data byte array.
        midiData.read(0, this.midiData, 0, this.midiDataSize);
    }

    /***
     * Returns the message length based on status byte.
     * @param byte0 status byte of message to be measured.
     * @return int
     */
    public static int getMessageLength(int byte0) {
        int cmd = byte0 & 0xFF;

        int length = -1;

        if (cmd >= 0x80 && cmd <= 0xBF || cmd >= 0xE0 && cmd <= 0xEF || cmd == 0xF2) {
            length = 3;
        } else if (cmd >= 0xC0 && cmd <= 0xDF || cmd == 0xF3 || cmd == 0xF5) {
            length = 2;
        } else if (cmd != 0xF0 && cmd != 0xF7){
            length = 1;
        }

        return length;
    }

    /***
     * Returns the name of a midi note number assuming sharpened notes
     * with Octave number appended and octave 3 for Middle C.
     * E.g "C5", "D#3", "--" etc.
     *
     * @param noteNumber            the midi note number, 0 to 127
     * @return                      String
     */
    public static String getMidiNoteName(int noteNumber)
    {
        if (noteNumber > 0 && noteNumber < 128) {
            String s = getMidiNoteName(noteNumber, true, true, 3);
            return s;
        }

        return "--";
    }

    /**
     * Returns the name of a midi note number.
     * E.g "C5", "D#3", "--" etc.
     *
     * @param noteNumber            the midi note number, 0 to 127
     * @param useSharps             if true, sharpened notes are used, e.g. "C#", otherwise they'll be flattened, e.g. "Db"
     * @param includeOctaveNumber   if true, the octave number will be appended to the string, e.g. "C#4"
     * @param octaveNumForMiddleC   if an octave number is being appended, this indicates the number that will be used for middle C's octave
     * @return                      String
     */
    public static String getMidiNoteName(int noteNumber,
                                         boolean useSharps,
                                         boolean includeOctaveNumber,
                                         int octaveNumForMiddleC)
    {
        String[] sharpNoteNames = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
        String[] flatNoteNames = {"C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B"};

        if (noteNumber > 0 && noteNumber < 128) {
            String s = (useSharps ? sharpNoteNames[noteNumber % 12] : flatNoteNames[noteNumber % 12]);
            if (includeOctaveNumber) {
                s += (noteNumber / 12 + (octaveNumForMiddleC - 5));
            }

            return s;
        }

        return "--";
    }

    /***
     *  Returns the number of a midi note name
     * @param   noteName            E.g "C#3" or "Cb3" etc.
     * @param octaveNumForMiddleC   if an octave number is being appended, this indicates the number
     *                              that will be used for middle C's octave
     *
     * @return                      the corresponding note number.
     */
    public static int getMidiNoteNumber(String noteName, int octaveNumForMiddleC) {

        int noteNumber = -1;
        String[] sharpNoteNames = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
        String[] flatNoteNames = {"C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B"};

        if (noteName.equals("") ||
                !Arrays.toString(sharpNoteNames).contains(noteName.substring(0,1)) ||
                !Character.isDigit(noteName.charAt(noteName.length() - 1)))
        {
            throw new MidiException("Please enter a valid note name \"A, B, C, D, E, F or G\" " +
                    "followed by '#' or 'b' and an octave number between 1 to 9, i.e 'C#3'");
        }

        int octaveNumber = Integer.parseInt(noteName.substring(noteName.length() - 1));

        String note = noteName.substring(0, noteName.length() - 1);
        String[] noteNames = sharpNoteNames;

        if (Misc.getSymbolIndex(noteName) == -1) { // its a flat
            noteNames = flatNoteNames;
        }

        for (int i = 0; i < noteNames.length; i++) {
            if (noteNames[i].equals(note)) {
                noteNumber = (5 - octaveNumForMiddleC + octaveNumber) * 12 + i;
                break;
            }
        }

        return noteNumber;
    }

    /***
     * Creates a system-exclusive message.
     * The data passed in is wrapped with header and tail bytes of 0xF0 and 0xF7.
     *
     * @param sysexData     midi sysex message
     * @param dataSize      midi sysex message size
     * @param timeStamp     time stamp
     * @return              Well formed Sysex midi message
     */
    public static MidiMessage createSysExMessage(byte[] sysexData, int dataSize, double timeStamp) {
        if (dataSize < 1) {
            throw new MidiException("A Sysex midi Message size should be > 0");
        }

        if (sysexData == null) {
            throw new NullPointerException("A Sysex midi Message can't be null");
        }

        boolean headerExist = sysexData[0] == (byte) 0xF0;
        boolean tailExist = sysexData[dataSize - 1] == (byte) 0xF7;

        // Well formed sysex? Return new Sysex midi message.
        if (headerExist && tailExist) {
            return new MidiMessage(sysexData, dataSize, 0);

        }

        int sizeIncrease = 0;

        // Header or tail byte missing? Increase result array size by one...
        if (headerExist || tailExist) {
            sizeIncrease = 1;

            //... and by 2 if both are missing.
        } else {
            sizeIncrease = 2;
        }

        // Create our result array with correct size...
        byte[] result = new byte[dataSize + sizeIncrease];

        int destPos = 0;

        //... add the header if missing...
        if (!headerExist) {
            result[0] = (byte) 0xF0;
            destPos = 1;
        }
        //... add original data into result array...
        System.arraycopy(sysexData, 0, result, destPos, dataSize);

        //... add tail if missing.
        if (!tailExist) {
            result[dataSize + sizeIncrease - 1] = (byte) 0xF7;
        }

        //Time to return our freshly created Sysex midi message.
        return new MidiMessage(result, dataSize + sizeIncrease, 0);
    }

    /***
     * Create a status byte from the command nibble value and a midi channel value.
     *
     * @param command value in the range of 0 to 15
     * @param channel value in the range of 1 to 16
     * @return int of a complete status byte
     */
    public static int createStatusByte(int command, int channel) {
        return ((command & 0xF0) | ((channel - 1) & 0x0F));
    }

    /***
     * Get status byte of current midi message instance.
     *
     * @return int current status byte
     */
    public int getStatusByte() {
        if (midiDataSize > 0 )
            return (midiData[0] & 0xFF);

        return 0;
    }

    /**
     * Creates a key-down message (using an integer velocity).
     *
     * @param channel       the midi channel, in the range 1 to 16
     * @param noteNumber    the key number, 0 to 127
     * @param velocity      in the range 0 to 127
     * @param timeStamp     microseconds
     * @return              MidiMessage
     * @see                 MidiMessage#isNoteOn
     */
    public static MidiMessage noteOn(int channel, int noteNumber, int velocity, double timeStamp) {
        return new MidiMessage(createStatusByte(0x90, channel), (noteNumber & 127), velocity, timeStamp);
    }

    /**
     * Creates a key-down message (using a floating-point velocity).
     *
     * @param channel       the midi channel, in the range 1 to 16
     * @param noteNumber    the key number, 0 to 127
     * @param velocity      in the range 0 to 1.0
     * @param timeStamp     microseconds
     * @return              MidiMessage
     * @see                 MidiMessage#isNoteOn
     */
    public static MidiMessage noteOn(int channel, int noteNumber, float velocity, double timeStamp) {
        return noteOn(channel, noteNumber, (int) (127.0f * velocity), timeStamp);
    }

    /**
     * Creates a key-up message.
     *
     * @param channel       the midi channel, in the range 1 to 16
     * @param noteNumber    the key number, 0 to 127
     * @param velocity      in the range 0 to 127
     * @param timeStamp     microseconds
     * @return              MidiMessage
     * @see                 MidiMessage#isNoteOff
     */
    public static MidiMessage noteOff(int channel, int noteNumber, int velocity, double timeStamp) {
        return new MidiMessage(createStatusByte(0x80, channel), noteNumber, velocity, timeStamp);
    }

    /**
     * Creates a key-up message.
     *
     * @param channel       the midi channel, in the range 1 to 16
     * @param noteNumber    the key number, 0 to 127
     * @param velocity      in the range 0 to 1.0
     * @param timeStamp     microseconds
     * @return              MidiMessage
     * @see                 MidiMessage#isNoteOff
     */
    public static MidiMessage noteOff(int channel, int noteNumber, float velocity, double timeStamp) {
        return noteOff(channel, noteNumber, (int) (127.0f * velocity), timeStamp);
    }

    /**
     * Creates a key-up message.@param channel
     * the midi channel, in the range 1 to 16
     *
     * @param channel       the midi channel, in the range 1 to 16
     * @param noteNumber    the key number, 0 to 127
     * @param timeStamp     microseconds
     * @return              MidiMessage
     * @see                 MidiMessage#isNoteOff
     */
    public static MidiMessage noteOff(int channel, int noteNumber, double timeStamp) {
        return noteOff(channel, noteNumber, 0, timeStamp);
    }

    /**
     * Creates a program-change message.
     *
     * @param channel       the midi channel, in the range 1 to 16
     * @param programNumber the midi program number, 0 to 127
     * @param timeStamp     microseconds
     * @return              MidiMessage
     * @see                 MidiMessage#isProgramChange
     * @see                 MidiMessage#MidiMessage#getGMInstrumentName
     */
    static MidiMessage programChange(int channel, int programNumber, double timeStamp) {
        return new MidiMessage(createStatusByte(0xC0, channel), programNumber & 0x7F, 0);
    }

    /**
     * Creates a pitch-wheel move message.
     *
     * @param channel       the midi channel, in the range 1 to 16
     * @param position      the wheel position, in the range 0 to 16383
     * @param timeStamp     microseconds
     * @return              MidiMessage
     * @see                 MidiMessage#isPitchWheel
     */
    static MidiMessage pitchWheel(int channel, int position, double timeStamp) {
        return new MidiMessage(createStatusByte(0xE0, channel), position & 127, (position >> 7) & 127, 0);
    }

    /**
     * Creates a channel-pressure change event.
     *
     * @param channel       the midi channel: 1 to 16
     * @param pressure      the pressure, 0 to 127
     * @param timeStamp     microseconds
     * @return              MidiMessage
     * @see                 MidiMessage#isChannelPressure()
     */
    static MidiMessage channelPressureChange(int channel, int pressure, double timeStamp) {
        return new MidiMessage(createStatusByte(0xD0, channel), pressure & 0x7F, 0);
    }

    /**
     * Creates an aftertouch message.
     *
     * @param channel           the midi channel, in the range 1 to 16
     * @param noteNumber        the key number, 0 to 127
     * @param aftertouchAmount  the amount of aftertouch, 0 to 127
     * @param timeStamp         microseconds
     * @return                  MidiMessage
     * @see                     MidiMessage#isPolyAftertouch()
     */
    static MidiMessage aftertouchChange(int channel, int noteNumber, int aftertouchAmount, double timeStamp) {
        return new MidiMessage(createStatusByte(0xA0, channel), noteNumber & 0x7F, aftertouchAmount & 0x7F, 0);
    }

    /**
     * Creates a controller message.
     *
     * @param channel           the midi channel, in the range 1 to 16
     * @param controllerType    the type of controller
     * @param value             the controller value
     * @param timeStamp         time stamp
     * @return                  MidiMessage
     * @see                     MidiMessage#isController()
     */
    static MidiMessage controllerEvent(int channel, int controllerType, int value, double timeStamp) {
        return new MidiMessage(createStatusByte(0xB0, channel), (controllerType & 127), (value & 127), 0);
    }

    /**
     * Creates an all-notes-off message.
     *
     * @param channel the midi channel, in the range 1 to 16
     * @return                  MidiMessage
     * @see                 MidiMessage#isAllNotesOff
     */
    public static MidiMessage allNotesOff(int channel) {
        return controllerEvent(channel, 123, 0, 0);
    }

    /**
     * Creates an all-sound-off message.
     *
     * @param channel the midi channel, in the range 1 to 16
     * @return                  MidiMessage
     * @see                 MidiMessage#isAllSoundOff
     */
    public static MidiMessage allSoundOff(int channel) {
        return controllerEvent(channel, 120, 0, 0);
    }

    /**
     * Creates an all-controllers-off message.
     *
     * @param channel the midi channel, in the range 1 to 16
     * @return                  MidiMessage
     * @see                 MidiMessage#isResetAllControllers
     */
    public static MidiMessage allControllersOff(int channel) {
        return controllerEvent(channel, 121, 0, 0);
    }

    /***
     * Get the current midi data block
     *
     * @return byte[]
     */
    public byte[] getMidiData() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't return it.");
        }

        return midiData;
    }

    /***
     * Get the current midi data block size
     *
     * @return int
     */
    public int getMidiDataSize() {
        if (midiDataSize < 1) {
            throw new MidiException("midiDataSize must be > 0");
        }

        return midiDataSize;
    }

    /**
     * Returns a human-readable description of the midi message as a string
     *
     * @return "Note On C#3 Velocity 120 Channel 1"
     */
    public String getDescription() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't return its description");
        }

        if (isNoteOn(false)) {
//            return String.format("%02d:%02d:%02d:%03d - ",hours, minutes, seconds, millis);

            return String.format(
                    "Note ON  %-4s Velocity %03d Channel %02d",
                    getMidiNoteName(getNoteNumber(), true, true, 3),
                    getVelocity(),
                    getChannel()
            );
        }

        if (isNoteOff(true)) {
            return String.format(
                    "Note OFF %-4s Velocity %03d Channel %02d",
                    getMidiNoteName(getNoteNumber(), true, true, 3),
                    getVelocity(),
                    getChannel()
            );
        }

        if (isProgramChange()) {
            return String.format(
                    "Program change %03d Channel %02d",
                    getProgramChangeNumber(),
                    getChannel()
            );
        }

        if (isPitchWheel()) {
            return String.format(
                    "Pitchbend %05d Channel %02d",
                    getPitchWheelValue(),
                    getChannel()
            );
        }

        if (isPolyAftertouch()) {
            return String.format(
                    "Poly Aftertouch %-4s: %03d Channel %02d",
                    getMidiNoteName(getNoteNumber(), true, true, 3),
                    getPolyAftertouchValue(),
                    getChannel()
            );
        }

        if (isChannelPressure()) {
            return String.format(
                    "Channel Aftertouch %03d Channel %02d",
                    getChannelPressureValue(),
                    getChannel()
            );
        }

        if (isAllNotesOff()) {
            return String.format(
                    "All notes off Channel %02d",
                    getChannel()
            );
        }

        if (isAllSoundOff()) {
            return String.format(
                    "All sound off Channel %02d",
                    getChannel()
            );
        }

        if (isSysEx()) {
            return String.format(
                    "SysEx: %-4s",
                    midiDataToHexString()
            );
        }

        if (isMetaEvent()) {
            return "Meta event";
        }

        if (isController()) {
            String name = getControllerName(getControllerNumber());

            if (name.isEmpty())
                name = String.valueOf(getControllerNumber());

            return String.format(
                    "CC %s: %03d Channel %02d",
                    name.equals("--") ? getControllerNumber() : name,
                    getControllerValue(),
                    getChannel()
            );
        }

        return "Midi message description (HexString): " + midiDataToHexString();
    }

    /***
     * Returns raw midi data as a HexString.
     *
     * @return String i.e. "Status 0x9F(159), 0x3D(61), 0x7C(124)"
     */
    public String midiDataToHexString() {
        String hexString = "No Midi data to process!";

        if (midiData == null || (midiData[0] & 0xFF) < 1 || midiDataSize < 1) {
            return hexString;
        }

        for (int i = 0; i < midiDataSize; i++) {

            if (i == 0)
                hexString = (midiData[0] & 0xFF) == 0xF0 ? "Header " : "Status ";

            else if (i == midiDataSize - 1 && isSysEx())
                hexString += (midiData[0] & 0xFF) == 0xF0 ? "Tail " : " ";

            hexString += String.format(
                    "0x%s(%02d)",
                    Integer.toHexString(midiData[i] & 0xFF).toUpperCase(),
                    midiData[i] & 0xFF
            );
            hexString += i < midiDataSize - 1 ? ", " : "";

//                hexString += "0x" + Integer.toHexString(midiData[i] & 0xFF) + "(" + (midiData[i] & 0xFF) + "), ";
        }
        return hexString;
    }

    /***
     * Returns this message's timestamp.
     *
     * @return double
     */
    public double getTimeStamp() {
        return timeStamp;
    }

    /***
     * Changes the message's associated timestamp.
     *
     * @param newTimestamp microseconds
     */
    public void setTimeStamp(double newTimestamp) {
        timeStamp = newTimestamp;
    }

    /***
     * Adds a value to the message's timestamp.
     *
     * @param delta the amount by which to increase the time stamp
     */
    public void addToTimeStamp(double delta) {
        timeStamp += delta;
    }

    /**
     * Return a copy of this message with a new timestamp.
     *
     * @param newTimestamp the new timestamp in microseconds
     * @return MidiMessage
     * @throws CloneNotSupportedException --
     */
    public MidiMessage withTimeStamp(double newTimestamp) throws CloneNotSupportedException {
        MidiMessage midiMessage = null;
        midiMessage = (MidiMessage) this.clone();

        if (midiMessage == null) {
            throw new NullPointerException("Attempt to clone current midiMessage and change its timestamp failed.");
        }

        midiMessage.timeStamp = newTimestamp;

        return midiMessage;
    }

    /**
     * Returns current message's midi channel.
     *
     * @return int
     */
    public int getChannel() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't get the Channel number.");
        }

        if ((midiData[0] & 0xF0) == 0xF0) {
            return 0;
        }

        return (midiData[0] & 0xF) + 1;
    }

    /**
     * Changes the message's midi channel.
     *
     * @param number the midi channel, in the range 1 to 16
     */
    public void setChannel(int number) {
        if (number < 1 || number > 16) {
            throw new MidiException("A Midi voice channel can only be between 1 and 16");
        }

        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't set the Channel to " + number);
        }

        midiData[0] = (byte) ((midiData[0] & 0xF0) | ((number - 1) & 0xF));
    }

    /**
     * Returns true if the message applies to the given midi channel.
     *
     * @param number the midi channel, in the range 1 to 16
     * @return boolean
     */
    public boolean isForChannel(int number) {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't check if current message applies to channel " + number);
        }

        return (midiData[0] & 0xF) + 1 == number;
    }

    /**
     * Returns true if this is a system-exclusive message.
     *
     * @return boolean
     */
    public boolean isSysEx() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't check if it is a SysEx.");
        }

        return (midiData[0] & 0xF0) == 0xF0;

    }

    /**
     * Returns a byte array of sysex data inside the message.
     *
     * @return byte[]
     */
    public byte[] getSysExData() {
        return isSysEx() ? midiData : null;
    }

    /**
     * Returns the size of the sysex data.
     *
     * @return int
     */
    public int getSysExDataSize() {
        if (midiDataSize < 1) {
            throw new MidiException("A SysEx message size must be > 0.");
        }

        return isSysEx() ? midiDataSize - 2 : 0;

    }

    /**
     * Returns true if this message is a 'key-down' event.
     *
     * @param returnTrueForVelocity0    set to true to return that it is a note on
     *                                  event when velocity is 0 the opposite otherwise.
     * @return                          boolean
     */
    public boolean isNoteOn(boolean returnTrueForVelocity0) {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't check if it is a Note ON.");
        }

        return ((midiData[0] & 0xF0) == 0x90) && (returnTrueForVelocity0 || midiData[2] != 0);
    }

    /**
     * Returns true if this message is a 'key-up' event.
     *
     * @param returnTrueForNoteOnVelocity0  set to true to return that it is a note on
     *                                      event when velocity is 0 the opposite otherwise.
     * @return                              boolean
     */
    public boolean isNoteOff(boolean returnTrueForNoteOnVelocity0) {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't check if it is a NoteOFF.");
        }

        return ((midiData[0] & 0xF0) == 0x80)
                || (midiData.length == 3)
                && (returnTrueForNoteOnVelocity0
                && (midiData[2] == 0)
                && ((midiData[0] & 0xF0) == 0x90));
    }

    /**
     * Returns true if this message is a 'key-down' or 'key-up' event.
     *
     * @return boolean
     */
    public boolean isNoteOnOrOff() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't check if it is a NoteON or NoteOFF.");
        }

        return ((midiData[0] & 0xF0) == 0x90 || (midiData[0] & 0xF0) == 0x80);

    }

    /**
     * Returns the midi note number for note-on and note-off messages.
     *
     * @return int
     */
    public int getNoteNumber() {

        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't get Note Number.");
        }

        return midiData[1];

    }

    /**
     * Changes the midi note number of a note-on or note-off message.
     *
     * @param newNoteNumber the midi note number, in the range 1 to 127
     */
    public void setNoteNumber(int newNoteNumber) {
        int result = -1;

        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't set a new note number.");
        }

        if (isNoteOnOrOff() || isPolyAftertouch() || isChannelPressure())
            midiData[1] = (byte)newNoteNumber;
    }

    /**
     * Returns the velocity of a note-on or note-off message.
     *
     * @return int
     */
    public int getVelocity() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't get Velocity.");
        }

        if (isNoteOnOrOff())
            return midiData[2];

        return 0;
    }

    /**
     * Changes the velocity of a note-on or note-off message.
     *
     * @param newVelocity the note velocity, in the range 0.0F to 1.0F
     */
    public void setFloatVelocity(float newVelocity) {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't set a new velocity.");
        }

        if (isNoteOnOrOff())
            midiData[2] = (byte) (127.0f * newVelocity);
    }

    /**
     * Changes the velocity of a note-on or note-off message.
     *
     * @param newVelocity the note velocity, in the range 0 to 127
     */
    public void setVelocity(int newVelocity) {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't set a new velocity.");
        }

        if (isNoteOnOrOff())
            midiData[2] = (byte) newVelocity;
    }

    /**
     * Returns the velocity of a note-on or note-off message.
     *
     * @return float
     */
    public float getFloatVelocity() {
        return (getVelocity() * 1.0f) / 127.0f;
    }

    /**
     * Multiplies the velocity of a note-on or note-off message by a given amount.
     *
     * @param scaleFactor   the note velocity multiplicand, in the range 0.0F to 1.0F
     */
    public void multiplyVelocity(float scaleFactor) {
        setFloatVelocity(getFloatVelocity() * scaleFactor);
    }

    /**
     * Returns true if this message is a 'sustain pedal down' controller message.
     *
     * @return  boolean
     */
    public boolean isSustainPedalOn() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't check if it is a Sustain Pedal ON.");
        }

        return isControllerOfType(0x40) && midiData[2] >= 64;

    }

    /**
     * Returns true if this message is a 'sustain pedal up' controller message.
     *
     * @return  boolean
     */
    public boolean isSustainPedalOff() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't check if it is a Sustain Pedal OFF.");
        }

        return isControllerOfType(0x40) && midiData[2] < 64;

    }

    /**
     * Returns true if this message is a 'sostenuto pedal down' controller message.
     *
     * @return  boolean
     */
    public boolean isSostenutoPedalOn() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't check if it is a Sostenuto Pedal ON.");
        }

        return isControllerOfType(0x42) && midiData[2] >= 64;

    }

    /**
     * Returns true if this message is a 'sostenuto pedal up' controller message.
     *
     * @return  boolean
     */
    public boolean isSostenutoPedalOff() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't check if it is a Sostenuto Pedal OFF.");
        }

        return isControllerOfType(0x42) && midiData[2] < 64;

    }

    /**
     * Returns true if this message is a 'soft pedal down' controller message.
     *
     * @return  boolean
     */
    public boolean isSoftPedalOn() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't check if it is a Soft Pedal ON.");
        }

        return isControllerOfType(0x43) && midiData[2] >= 64;

    }

    /**
     * Returns true if this message is a 'soft pedal up' controller message.
     *
     * @return  boolean
     */
    public boolean isSoftPedalOff() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't check if it is a Soft Pedal OFF.");
        }

        return isControllerOfType(0x43) && midiData[2] < 64;

    }

    /**
     * Returns true if the message is a program (patch) change message.
     *
     * @return  boolean
     */
    public boolean isProgramChange() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't check if it is a Program Change.");
        }

        return (midiData[0] & 0xF0) == 0xC0;

    }

    /**
     * Returns the new program number of a program change message.
     *
     * @return  int
     */
    public int getProgramChangeNumber() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't get a Program Change number.");
        }

        return midiData[1];

    }

    /**
     * Returns true if the message is a pitch-wheel move.
     *
     * @return  boolean
     */
    public boolean isPitchWheel() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't check if it is a Pitch Wheel");
        }

        return (midiData[0] & 0xF0) == 0xE0;

    }

    /**
     * Returns the pitch wheel position from a pitch-wheel move message.
     *
     * @return int
     */
    public int getPitchWheelValue() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't get a Pitch Wheel value.");
        }

        return midiData[1] | midiData[2] << 7;

    }

    /**
     * Returns true if the message is a channel-pressure change event.
     *
     * @return  boolean
     */
    public boolean isChannelPressure() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't check if it is a Channel Pressure.");
        }

        return ((midiData[0] & 0xF0) == 0xD0);

    }

    /**
     * Returns the pressure from a channel pressure change message.
     *
     * @return int
     */
    public int getChannelPressureValue() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't get a Channel Pressure value.");
        }

        if (isChannelPressure()) {
            return midiData[1];
        }

        return 0;
    }

    /**
     * Returns true if the message is a Polyphonic Aftertouch event.
     *
     * @return  boolean
     */
    public boolean isPolyAftertouch() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't check if it is a Poly After Touch.");
        }

        return ((midiData[0] & 0xF0) == 0xA0);

    }

    /**
     * Returns the amount of Poliphonic Aftertouch from an Aftertouch messages.
     *
     * @return  int
     */
    public int getPolyAftertouchValue() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't get a Poly After Touch value.");
        }

        if (isPolyAftertouch()) {
            return midiData[2];
        }

        return 0;
    }

    /**
     * Returns true if this is a midi controller message.
     *
     * @return  boolean
     */
    public boolean isController() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't check if it is a Controller.");
        }

        return ((midiData[0] & 0xF0) == 0xB0);

    }

    /**
     * Return the name of the midi chosen controller if available, "--" otherwise.
     *
     * @param controllerNumber the controller number in the range of 0 to 127
     * @return String
     */
    public String getControllerName(int controllerNumber) {
        String[] ctrlNames = {
                "Bank Select", "Modulation Wheel (coarse)", "Breath controller (coarse)",
                "--",
                "Foot Pedal (coarse)", "Portamento Time (coarse)", "Data Entry (coarse)",
                "Volume (coarse)", "Balance (coarse)",
                "--",
                "Pan position (coarse)", "Expression (coarse)", "Effect Control 1 (coarse)",
                "Effect Control 2 (coarse)",
                "--", "--",
                "General Purpose Slider 1", "General Purpose Slider 2",
                "General Purpose Slider 3", "General Purpose Slider 4",
                "--", "--", "--", "--", "--", "--", "--", "--", "--", "--", "--", "--",
                "Bank Select (fine)", "Modulation Wheel (fine)", "Breath controller (fine)",
                "--",
                "Foot Pedal (fine)", "Portamento Time (fine)", "Data Entry (fine)", "Volume (fine)",
                "Balance (fine)", "--", "Pan position (fine)", "Expression (fine)",
                "Effect Control 1 (fine)", "Effect Control 2 (fine)",
                "--", "--", "--", "--", "--", "--", "--", "--", "--",
                "--", "--", "--", "--", "--", "--", "--", "--", "--",
                "Hold Pedal (on/off)", "Portamento (on/off)", "Sustenuto Pedal (on/off)", "Soft Pedal (on/off)",
                "Legato Pedal (on/off)", "Hold 2 Pedal (on/off)", "Sound Variation", "Sound Timbre",
                "Sound Release Time", "Sound Attack Time", "Sound Brightness", "Sound Control 6",
                "Sound Control 7", "Sound Control 8", "Sound Control 9", "Sound Control 10",
                "General Purpose Button 1 (on/off)", "General Purpose Button 2 (on/off)",
                "General Purpose Button 3 (on/off)", "General Purpose Button 4 (on/off)",
                "--", "--", "--", "--", "--", "--", "--",
                "Reverb Level", "Tremolo Level", "Chorus Level", "Celeste Level",
                "Phaser Level", "Data Button increment", "Data Button decrement", "Non-registered Parameter (fine)",
                "Non-registered Parameter (coarse)", "Registered Parameter (fine)", "Registered Parameter (coarse)",
                "--", "--", "--", "--", "--", "--", "--", "--", "--",
                "--", "--", "--", "--", "--", "--", "--", "--", "--",
                "All Sound Off", "All Controllers Off", "Local Keyboard (on/off)", "All Notes Off",
                "Omni Mode Off", "Omni Mode On", "Mono Operation", "Poly Operation"
        };

        return ctrlNames[controllerNumber];
    }

    /**
     * Returns the controller number of a controller message.
     *
     * @return int
     */
    public int getControllerNumber() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't get a Controller number.");
        }

        return midiData[1];

    }

    /**
     * Returns the controller value from a controller message.
     *
     * @return int
     */
    public int getControllerValue() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't get a Controller value.");
        }

        return midiData[2];

    }

    /**
     * Returns true if this message is a controller message and if it has the specified controller type.
     *
     * @param controllerType    the midi controller type in the range of 0 to 127
     * @return                  boolean
     */
    public boolean isControllerOfType(int controllerType) {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't check type of Controller.");
        }

        return ((midiData[0] & 0xF0) == 0xB0) && (midiData[1] == controllerType);

    }

    /**
     * Checks whether this message is an all-notes-off message.
     *
     * @return boolean
     */
    public boolean isAllNotesOff() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't check if it is an All Note OFF");
        }

        return ((midiData[0] & 0xF0) == 0xB0) && (midiData[1] == 123);
    }

    /**
     * Checks whether this message is an all-sound-off message.
     *
     * @return  boolean
     */
    public boolean isAllSoundOff() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't check if it is an All Sound OFF.");
        }

        return (midiData[1] == 120) && ((midiData[0] & 0xF0) == 0xB0);
    }

    /**
     * Checks whether this message is a reset all controllers message.
     *
     * @return boolean
     */
    public boolean isResetAllControllers() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't check if it is a Reset All Controllers.");
        }

        return ((midiData[0] & 0xF0) == 0xB0) && (midiData[1] == 121);
    }

    /***
     * Convert timestamp to SMPTE timecode format.
     *
     * @return String (i.e. 03:06:40:000 - )
     */
    public String timeStampAsTimecode() {
        return SmpteTimecode.getTimecode(timeStamp * 1000);
    }

    /**
     * Returns true if this event is a meta-event.
     *
     * @return  boolean
     */
    public boolean isMetaEvent() {
        if (midiData == null) {
            throw new NullPointerException("midiData is 'null' - can't check if it is a Meta Event.");
        }

        return (midiData[0] & 0xFF) == 0xFF;
    }
}
        
