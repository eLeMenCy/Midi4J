package com.elemency.Midi4J;

import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.sun.jna.Pointer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class MidiMessageTest {

    final byte[] sysexExpected = {(byte) 0xF0, 61, 82, 124, 6, (byte) 0xF7};
    MidiMessage msg;

    @Nested
    class MidiMessageConstructors {

        @Test
//    @Disabled
        void constructor3BytesMidiMessage()
        {
            msg = new MidiMessage(0x90, 65, 120, 0);
            assertEquals(1, msg.getChannel());
            assertEquals(65, msg.getNoteNumber());
            assertEquals(120, msg.getVelocity());
            assertEquals(0.94F, msg.getFloatVelocity(), 0.01);
            assertEquals(3, MidiMessage.getMessageLength(0x90));
            assertEquals(3, msg.getMidiDataSize());
            assertEquals(0x90, msg.getStatusByte());
            assertEquals(0, msg.getTimeStamp());
            assertTrue(msg.isNoteOn(true));

            assertThrows(MidiException.class, () -> new MidiMessage(0xC0, 65, 120, 0));
            assertThrows(MidiException.class, () -> new MidiMessage(0xF0, 65, 120, 0));
        }

        @Test
//    @Disabled
        void constructor2BytesMidiMessage()
        {
            msg = new MidiMessage(0xC0, 65, 0);
            assertEquals(65, msg.getNoteNumber());
            assertEquals(2, MidiMessage.getMessageLength(0xC0));
            assertEquals(2, msg.getMidiDataSize());
            assertEquals(0xC0, msg.getStatusByte());
            assertEquals(0, msg.getTimeStamp());
            assertTrue(msg.isProgramChange());

            assertThrows(MidiException.class, () -> new MidiMessage(0x90, 65, 0));
            assertThrows(MidiException.class, () -> new MidiMessage(0xF0, 65, 0));
        }

        @Test
//        @Disabled
        void constructor1BytesMidiMessage()
        {
            msg = new MidiMessage(0xF8, 0);
            assertEquals(1, MidiMessage.getMessageLength(0xF8));
            assertEquals(1, msg.getMidiDataSize());
            assertEquals(0xF8, msg.getStatusByte()/* | 0xF8*/);
            assertEquals(0, msg.getTimeStamp());

            assertThrows(MidiException.class, () -> new MidiMessage(0x90,0));
//            assertThrows(MidiException.class, () -> new MidiMessage(0xF0,0));
        }

        @Test
        void constructorByteArrayMidiMessage() {
            final byte[] midiData = {(byte) 0x9F, 61, 124};
            msg = new MidiMessage(midiData, 3, 6);
            assertEquals(16, msg.getChannel());
            assertEquals(61, msg.getNoteNumber());
            assertEquals(124, msg.getVelocity());
            assertEquals(0.98F, msg.getFloatVelocity(), 0.01);
            assertEquals(3, MidiMessage.getMessageLength(0x90));
            assertEquals(3, msg.getMidiDataSize());
            assertEquals(0x9F, msg.getStatusByte());
            assertEquals(6, msg.getTimeStamp());
            assertTrue(msg.isNoteOn(true));

            assertThrows(MidiException.class, () -> new MidiMessage(midiData, 0, 6));
            assertThrows(MidiException.class, () -> new MidiMessage(null, 0, 6));
        }

        @Test
//        @Disabled
        void constructorNativeMidiMessage() {
            final byte[] byteBuffer = {(byte) 0x90, 61, 82};
            Pointer mp = null; //new Pointer(?);
            NativeSize mpSize = new NativeSize(3);
//            mp.write(0, byteBuffer, 0, 3);
//            msg = new MidiMessage(mp, mpSize, 0);

//            assertEquals(16, msg.getChannel());
//            assertEquals(61, msg.getNoteNumber());
//            assertEquals(124, msg.getVelocity());
//            assertEquals(0.98F, msg.getFloatVelocity(), 0.01);
//            assertEquals(3, MidiMessage.checkMessageLength(0x90));
//            assertEquals(3, msg.getMidiDataSize());
//            assertEquals(0x9F, msg.getStatusByte());
//            assertEquals(6, msg.getTimeStamp());
//            assertTrue(msg.isNoteOn(true));

//            assertThrows(MidiException.class, () -> new MidiMessage(mp, mpSize, 6));
            assertThrows(NullPointerException.class, () -> new MidiMessage(null, mpSize, 6));
        }
    }

    @ParameterizedTest()
    @CsvSource({
            "144, 3",
            "192, 2",
            "248, 1",
            "240, -1",
            "247, -1",
    })
    void checkMessageLength(int byte0, int expected) {
        assertEquals(expected, MidiMessage.getMessageLength(byte0), ()-> "Byte0= : " + byte0 + "Expected: " + expected);

    }

    @Test
    void getMidiNoteName() {
        assertEquals("C3", MidiMessage.getMidiNoteName(60, true, true, 3));
        assertEquals("--", MidiMessage.getMidiNoteName(129, true, true, 3));
    }


    @Test
    void getMidiNoteNumber() {
        assertEquals(78, MidiMessage.getMidiNoteNumber("F#4", 3));
        assertEquals(60, MidiMessage.getMidiNoteNumber("C3", 3));
        assertEquals(61, MidiMessage.getMidiNoteNumber("Db3", 3));
    }

    @Nested
    class CreateSysExMessage {

        // SysEx message test using createSysExMessage().
        @Test
        @DisplayName("Create SysEx with wellFormed Sysex byte array")
        void createSysExMessageWithWellFormed()
        {
            byte[] sysexWellFormed = sysexExpected;

            msg = MidiMessage.createSysExMessage(sysexWellFormed, sysexWellFormed.length, 0);
            assertArrayEquals(sysexExpected, msg.getMidiData(), "Given Sysex byte array should comeback as the same");
        }

        // SysEx message test using createSysExMessage().
        @Test
        @DisplayName("Create SysEx without a Sysex Header")
        void createSysExMessageWithoutHeader()
        {
            byte[] sysexNoHeader = {61, 82, 124, 6, (byte) 0xF7};

            msg = MidiMessage.createSysExMessage(sysexNoHeader, sysexNoHeader.length, 0);
            assertArrayEquals(sysexExpected, msg.getMidiData(),"Given missing header Sysex byte array should comeback as a well formed Sysex message");
        }

        // SysEx message test using createSysExMessage().
        @Test
        @DisplayName("Create SysEx without a Sysex Tail")
        void createSysExMessageWithoutTail()
        {
            byte[] sysexNoTail = {(byte) 0xF0, 61, 82, 124, 6};

            msg = MidiMessage.createSysExMessage(sysexNoTail, sysexNoTail.length, 0);
            assertArrayEquals(sysexExpected, msg.getMidiData(),"Given missing tail Sysex byte array should comeback as a well formed Sysex message");
        }

        // SysEx message test using createSysExMessage().
        @Test
        @DisplayName("Throw Exception ")
        void createSysExMessageException()
        {
            byte[] wellFormed = {(byte) 0xF0, 61, 82, 124, 6, (byte) 0xF7};
            assertThrows(MidiException.class, () -> MidiMessage.createSysExMessage(wellFormed, 0, 0), "Sysex byte array should throw a MidiException when size is < 1");
            assertThrows(NullPointerException.class, () -> MidiMessage.createSysExMessage(null, 6, 0), "Sysex byte array should throw a NullPointerException null");
        }
    }

    @Test
    void createStatusByte() {
        assertEquals(0x90, MidiMessage.createStatusByte(0x90, 1), "Channel messages are Ok");
        assertNotEquals(0xF1, MidiMessage.createStatusByte(0xF1, 1), "System messages are not OK as they do not include a channel");
    }

    @Test
    void getStatusByte() {
        final byte[] midiData = {(byte) 0x95, 61, 124};
        msg = new MidiMessage(midiData, 3, 6);
        assertEquals(0x95, msg.getStatusByte());
    }

    @Test
    void noteOnWithVelocityDouble() {
        msg = MidiMessage.noteOn(1, 60, 80, 0);
        assertEquals(1 , msg.getChannel());
        assertEquals(60 , msg.getNoteNumber());
        assertEquals(80 , msg.getVelocity());
        assertEquals(0.63F , msg.getFloatVelocity(), 0.01);
        assertEquals(3 , msg.getMidiDataSize());
        assertEquals(0x90 , msg.getStatusByte());
        assertEquals(0, msg.getTimeStamp());
        assertTrue(msg.isNoteOn(true));
    }

    @Test
    void testNoteOnwithVelocityFloat() {
        msg = MidiMessage.noteOn(1, 60, 0.8F, 0);
        assertEquals(1 , msg.getChannel());
        assertEquals(60 , msg.getNoteNumber());
        assertEquals(0.8F , msg.getFloatVelocity(), 0.01);
        assertEquals(101 , msg.getVelocity());
        assertEquals(3 , msg.getMidiDataSize());
        assertEquals(0x90 , msg.getStatusByte());
        assertEquals(0, msg.getTimeStamp());
        assertTrue(msg.isNoteOn(true));
    }

    @Test
    void noteOffWithWithoutVelocity() {
        msg = MidiMessage.noteOff(1, 60, 0);
        assertEquals(1 , msg.getChannel());
        assertEquals(60 , msg.getNoteNumber());
        assertEquals(0 , msg.getVelocity());
        assertEquals(0.0F , msg.getFloatVelocity(), 0.01);
        assertEquals(3 , msg.getMidiDataSize());
        assertEquals(0x80 , msg.getStatusByte());
        assertEquals(0, msg.getTimeStamp());
        assertTrue(msg.isNoteOff(true));
    }

    @Test
    void noteOffWithVelocityDouble() {
        msg = MidiMessage.noteOff(1, 60, 0, 0);
        assertEquals(1 , msg.getChannel());
        assertEquals(60 , msg.getNoteNumber());
        assertEquals(0 , msg.getVelocity());
        assertEquals(0.0F , msg.getFloatVelocity(), 0.01);
        assertEquals(3 , msg.getMidiDataSize());
        assertEquals(0x80 , msg.getStatusByte());
        assertEquals(0, msg.getTimeStamp());
        assertTrue(msg.isNoteOff(true));
    }

    @Test
    void noteOffWithVelocityFloat() {
        msg = MidiMessage.noteOff(1, 60, 0.0F, 0);
        assertEquals(1 , msg.getChannel());
        assertEquals(60 , msg.getNoteNumber());
        assertEquals(0 , msg.getVelocity());
        assertEquals(0.0F , msg.getFloatVelocity(), 0.01);
        assertEquals(3 , msg.getMidiDataSize());
        assertEquals(0x80 , msg.getStatusByte());
        assertEquals(0, msg.getTimeStamp());
        assertTrue(msg.isNoteOff(true));
    }

    @Test
    void programChange() {
        msg = MidiMessage.programChange(2, 34, 0);
        assertEquals(0xC1, msg.getStatusByte());
        assertEquals(2, msg.getChannel());
        assertEquals(34, msg.getProgramChangeNumber());
    }

    @Test
    void pitchWheel() {
        msg = MidiMessage.pitchWheel(2, 1200, 0);
        assertEquals(0xE1, msg.getStatusByte());
        assertEquals(2, msg.getChannel());
        assertEquals(1200, msg.getPitchWheelValue());
    }

    @Test
    void channelPressureChange() {
        msg = MidiMessage.channelPressureChange(2, 69, 0);
        assertEquals(0xD1, msg.getStatusByte());
        assertEquals(2, msg.getChannel());
        assertEquals(69, msg.getChannelPressureValue());
    }

    @Test
    void aftertouchChange() {
        msg = MidiMessage.aftertouchChange(2, 69, 120, 0);
        assertEquals(0xA1, msg.getStatusByte());
        assertEquals(2, msg.getChannel());
        assertEquals(69, msg.getNoteNumber());
        assertEquals(120, msg.getPolyAftertouchValue());
    }

    @Test
    void controllerEvent() {
        msg = MidiMessage.controllerEvent(2, 7, 120, 0);
        assertEquals(0xB1, msg.getStatusByte());
        assertEquals(2, msg.getChannel());
        assertEquals(7, msg.getControllerNumber());
        assertEquals(120, msg.getControllerValue());
    }

    @Test
    void allNotesOff() {
        msg = MidiMessage.allNotesOff(2);
        assertEquals(0xB1, msg.getStatusByte());
        assertEquals(2, msg.getChannel());
        assertEquals(123, msg.getControllerNumber());
    }

    @Test
    void allSoundOff() {
        msg = MidiMessage.allSoundOff(2);
        assertEquals(0xB1, msg.getStatusByte());
        assertEquals(2, msg.getChannel());
        assertEquals(120, msg.getControllerNumber());
    }

    @Test
    void allControllersOff() {
        msg = MidiMessage.allControllersOff(2);
        assertEquals(0xB1, msg.getStatusByte());
        assertEquals(2, msg.getChannel());
        assertEquals(121, msg.getControllerNumber());
    }

    @ParameterizedTest()
    @CsvSource({
//            "'255', 1, H",
            "'208,60', 2, Channel Aftertouch 060 Channel 01",
            "'192,34', 2, Program change 034 Channel 01",
            "'144,60,122', 3, Note ON  C3   Velocity 122 Channel 01",
            "'128,60,122', 3, Note OFF C3   Velocity 122 Channel 01",
            "'224,60,122', 3, Pitchbend 15676 Channel 01",
            "'160,60,122', 3, Poly Aftertouch C3  : 122 Channel 01",
            "'176,123,0', 3, All notes off Channel 01",
            "'176,120,0', 3, All sound off Channel 01",
            "'240,121,87,75,34,247', 6, 'SysEx: Header 0xF0(240), 0x79(121), 0x57(87), 0x4B(75), 0x22(34), Tail 0xF7(247)'",
            "'176,45,89', 3, CC Effect Control 2 (fine): 089 Channel 01",
            "'67,45,89', 3, 'Midi message description (HexString): Status 0x43(67), 0x2D(45), 0x59(89)'",

    })
    void getDescription(String data, int dataSize, String expected) {
        final String[] strArray = data.split(",");
        byte[] midiData = new byte[dataSize];

        System.out.println(data);
        for (int i = 0; i < dataSize; i++) {

            midiData[i] = (byte)Integer.parseInt(strArray[i]);
            System.out.println("strArray[" + i + "]: " + strArray[i] +
                    " midiData[" + i + "]: " + midiData[i]);
        }

        msg = new MidiMessage(midiData, dataSize, 6);
        assertEquals(expected, msg.getDescription());
    }

    @Test
    void midiDataToHexString() {
        final byte[] midiData = {(byte) 0x9F, 61, 124};
        msg = new MidiMessage(midiData, 3, 6);

        assertEquals("Status 0x9F(159), 0x3D(61), 0x7C(124)", msg.midiDataToHexString());
    }

    @Test
    void addToTimeStamp() {
        final byte[] midiData = {(byte) 0x9F, 61, 124};
        msg = new MidiMessage(midiData, 3, 6);
        msg.addToTimeStamp(3);
        assertEquals(9, msg.getTimeStamp());
    }

    @Test
    void withTimeStamp() throws CloneNotSupportedException {
        final byte[] midiData = {(byte) 0x9F, 61, 124};
        msg = new MidiMessage(midiData, 3, 6);

        assertNotEquals(6, msg.withTimeStamp(9));
    }

    @Test
    void getChannel() {
        final byte[] midiData = {(byte) 0x9F, 61, 124};
        msg = new MidiMessage(midiData, 3, 6);
        assertEquals(16, msg.getChannel());
    }

    @Test
    void setChannel() {
        final byte[] midiData = {(byte) 0x9F, 61, 124};
        msg = new MidiMessage(midiData, 3, 6);
        msg.setChannel(5);
        assertEquals(5, msg.getChannel());

        assertThrows(MidiException.class, () -> msg.setChannel(0));
        assertThrows(MidiException.class, () -> msg.setChannel(17));
        msg = new MidiMessage(null, 3, 6);
        assertThrows(MidiException.class, () -> msg.setChannel(0));
    }

    @Test
    void isForChannel() {
        final byte[] midiData = {(byte) 0x9F, 61, 124};
        msg = new MidiMessage(midiData, 3, 6);
        assertTrue(msg.isForChannel(16));
    }

    @Test
    void isSysEx() {
        final byte[] midiData = {(byte) 0xF0, 61, 124};
        msg = new MidiMessage(midiData, 3, 6);
        assertTrue(msg.isSysEx());
    }

    @Test
    void getSysExData() {
        final byte[] midiData = {(byte) 0xF0, 61, 124};
        msg = new MidiMessage(midiData, 3, 6);
        assertArrayEquals(midiData, msg.getSysExData());
    }

    @Test
    void isNoteOn() {
        byte[] midiData = {(byte) 0x90, 61, 124};
        msg = new MidiMessage(midiData, 3, 6);
        assertTrue(msg.isNoteOn(false));

        msg.setFloatVelocity(0);
        assertFalse(msg.isNoteOn(false));
    }

    @Test
    void isNoteOff() {
        byte[] midiData = {(byte) 0x80, 61, 124};
        msg = new MidiMessage(midiData, 3, 6);
        assertTrue(msg.isNoteOff(true));

        midiData[0] = (byte)0x90;
        msg.setFloatVelocity(0);
        assertTrue(msg.isNoteOff(true));
    }

    @Test
    void isNoteOnOrOff() {
        byte[] midiData = {(byte) 0x90, 61, 124};
        msg = new MidiMessage(midiData, 3, 6);
        assertTrue(msg.isNoteOnOrOff());

        midiData[0] = (byte)0x80;
        assertTrue(msg.isNoteOnOrOff());
    }

    @Test
    void getNoteNumber() {
        final byte[] midiData = {(byte) 0xF0, 61, 124};
        msg = new MidiMessage(midiData, 3, 6);
        assertEquals(61, msg.getNoteNumber());
    }

    @Test
    void setNoteNumber() {
        final byte[] midiData = {(byte) 0x9F, 61, 124};
        msg = new MidiMessage(midiData, 3, 6);
        msg.setNoteNumber(89);
        assertEquals(89, msg.getNoteNumber());
    }

    @Test
    void getVelocity() {
        final byte[] midiData = {(byte) 0x9F, 61, 124};
        msg = new MidiMessage(midiData, 3, 6);
        assertEquals(124, msg.getVelocity());
    }

    @Test
    void setVelocity() {
        final byte[] midiData = {(byte) 0x9F, 61, 124};
        msg = new MidiMessage(midiData, 3, 6);
        msg.setVelocity(114);
        assertEquals(114 , msg.getVelocity(), 0.01);
    }

    @Test
    void getFloatVelocity() {
        final byte[] midiData = {(byte) 0x9F, 61, 82};
        msg = new MidiMessage(midiData, 3, 6);
        assertEquals(0.64, msg.getFloatVelocity(), 0.01);
    }

    @Test
    void setFloatVelocity() {
        final byte[] midiData = {(byte) 0x9F, 61, 124};
        msg = new MidiMessage(midiData, 3, 6);
        msg.setFloatVelocity(0.6F);
        assertEquals(0.6F , msg.getFloatVelocity(), 0.01);
    }

    @Test
    void multiplyVelocity() {
        final byte[] midiData = {(byte) 0x9F, 61, 30};
        msg = new MidiMessage(midiData, 3, 6);
        msg.multiplyVelocity(2.0F);
        assertEquals(0.47F , msg.getFloatVelocity(), 0.01);
    }

    @Test
    void isSustainPedalOn() {
        final byte[] midiData = {(byte) 0xB0, 64, 127};
        msg = new MidiMessage(midiData, 3, 6);
        assertTrue(msg.isSustainPedalOn());
    }

    @Test
    void isSustainPedalOff() {
        final byte[] midiData = {(byte) 0xB0, 64, 0};
        msg = new MidiMessage(midiData, 3, 6);
        assertTrue(msg.isSustainPedalOff());
    }

    @Test
    void isSostenutoPedalOn() {
        final byte[] midiData = {(byte) 0xB0, 66, 127};
        msg = new MidiMessage(midiData, 3, 6);
        assertTrue(msg.isSostenutoPedalOn());
    }

    @Test
    void isSostenutoPedalOff() {
        final byte[] midiData = {(byte) 0xB0, 66, 0};
        msg = new MidiMessage(midiData, 3, 6);
        assertTrue(msg.isSostenutoPedalOff());
    }

    @Test
    void isSoftPedalOn() {
        final byte[] midiData = {(byte) 0xB0, 67, 127};
        msg = new MidiMessage(midiData, 3, 6);
        assertTrue(msg.isSoftPedalOn());
    }

    @Test
    void isSoftPedalOff() {
        final byte[] midiData = {(byte) 0xB0, 67, 0};
        msg = new MidiMessage(midiData, 3, 6);
        assertTrue(msg.isSoftPedalOff());
    }

    @Test
    void isProgramChange() {
        final byte[] midiData = {(byte) 0xC0, 67, 127};
        msg = new MidiMessage(midiData, 3, 6);
        assertTrue(msg.isProgramChange());
    }

    @Test
    void getProgramChangeNumber() {
        final byte[] midiData = {(byte) 0xC0, 67, 127};
        msg = new MidiMessage(midiData, 3, 6);
        assertEquals(67, msg.getProgramChangeNumber());
    }

    @Test
    void isPitchWheel() {
        final byte[] midiData = {(byte) 0xE0, 67, 127};
        msg = new MidiMessage(midiData, 3, 6);
        assertTrue(msg.isPitchWheel());
    }

    @Test
    void getPitchWheelValue() {
        final byte[] midiData = {(byte) 0xE0, 67, 127};
        msg = new MidiMessage(midiData, 3, 6);
        assertEquals(16323, msg.getPitchWheelValue());
    }

    @Test
    void isChannelPressure() {
        final byte[] midiData = {(byte) 0xD0, 67, 127};
        msg = new MidiMessage(midiData, 3, 6);
        assertTrue(msg.isChannelPressure());
    }

    @Test
    void getChannelPressureValue() {
        final byte[] midiData = {(byte) 0xD0, 67};
        msg = new MidiMessage(midiData, 3, 6);
        assertEquals(67, msg.getChannelPressureValue());
    }

    @Test
    void isPolyAftertouch() {
        final byte[] midiData = {(byte) 0xA0, 67, 127};
        msg = new MidiMessage(midiData, 3, 6);
        assertTrue(msg.isPolyAftertouch());
    }

    @Test
    void getPolyAftertouchValue() {
        final byte[] midiData = {(byte) 0xA0, 67, 127};
        msg = new MidiMessage(midiData, 3, 6);
        assertEquals(127, msg.getPolyAftertouchValue());
    }

    @Test
    void isController() {
        final byte[] midiData = {(byte) 0xB0, 67, 127};
        msg = new MidiMessage(midiData, 3, 6);
        assertTrue(msg.isController());
    }

    @Test
    void getControllerName() {
        final byte[] midiData = {(byte) 0xB0, 67, 127};
        msg = new MidiMessage(midiData, 3, 6);
        assertEquals("Soft Pedal (on/off)", msg.getControllerName(67));
    }

    @Test
    void getControllerNumber() {
        final byte[] midiData = {(byte) 0xB0, 67, 127};
        msg = new MidiMessage(midiData, 3, 6);
        assertEquals(67, msg.getControllerNumber());
    }

    @Test
    void getControllerValue() {
        final byte[] midiData = {(byte) 0xB0, 67, 127};
        msg = new MidiMessage(midiData, 3, 6);
        assertEquals(127, msg.getControllerValue());
    }


    @Test
    void isControllerOfType() {
        final byte[] midiData = {(byte) 0xB0, 67, 127};
        msg = new MidiMessage(midiData, 3, 6);
        assertTrue(msg.isControllerOfType(67));
    }

    @Test
    void isAllNotesOff() {
        final byte[] midiData = {(byte) 0xB0, 123};
        msg = new MidiMessage(midiData, 2, 6);
        assertTrue(msg.isAllNotesOff());
    }

    @Test
    void isAllSoundOff() {
        final byte[] midiData = {(byte) 0xB0, 120};
        msg = new MidiMessage(midiData, 2, 6);
        assertTrue(msg.isAllSoundOff());
    }

    @Test
    void isResetAllControllers() {
        final byte[] midiData = {(byte) 0xB0, 121};
        msg = new MidiMessage(midiData, 2, 6);
        assertTrue(msg.isResetAllControllers());
    }

    @Test
    void timeStampAsTimecode() {
        final byte[] midiData = {(byte) 0xB0, 121, 90};
        msg = new MidiMessage(midiData, 3, 11200);
        assertEquals("03:06:40:000 - ", msg.timeStampAsTimecode());
    }

    @Test
    void isMetaEvent() {
        final byte[] midiData = {(byte) 0xFF};
        msg = new MidiMessage(midiData, 1, 6);
        assertTrue(msg.isMetaEvent());
    }
}
