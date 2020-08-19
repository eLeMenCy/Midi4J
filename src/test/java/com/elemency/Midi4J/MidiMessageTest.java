package com.elemency.Midi4J;

import org.checkerframework.common.value.qual.StaticallyExecutable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.sound.midi.MidiEvent;

import static org.junit.jupiter.api.Assertions.*;

class MidiMessageTest {

    final byte[] sysexExpected = {(byte) 0xF0, 61, 82, 124, 6, (byte) 0xF7};
    MidiMessage msg;

    @AfterEach
    void tearDown() {
    }

    @BeforeEach
    void init() {
        msg = new MidiMessage();
    }

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
            assertEquals(3, MidiMessage.checkMessageLength(0x90));
            assertEquals(3, msg.getMidiDataSize());
            assertEquals(0x90, msg.getStatusByte());
            assertEquals(0, msg.getTimeStamp());
            assertTrue(msg.isNoteOn(true));
        }

        @Test
//    @Disabled
        void constructor2BytesMidiMessage()
        {
            msg = new MidiMessage(0xC0, 65, 0);
            assertEquals(65, msg.getNoteNumber());
            assertEquals(2, MidiMessage.checkMessageLength(0xC0));
            assertEquals(2, msg.getMidiDataSize());
            assertEquals(0xC0, msg.getStatusByte());
            assertEquals(0, msg.getTimeStamp());
            assertTrue(msg.isProgramChange());
        }

        @Test
//    @Disabled
        void constructor1BytesMidiMessage()
        {
            msg = new MidiMessage(0xF8, 0);
            assertEquals(1, MidiMessage.checkMessageLength(0xF8));
            assertEquals(1, msg.getMidiDataSize());
            assertEquals(0xF8, msg.getStatusByte()/* | 0xF8*/);
            assertEquals(0, msg.getTimeStamp());
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
        assertEquals(expected, MidiMessage.checkMessageLength(byte0), ()-> "Byte0= : " + byte0 + "Expected: " + expected);

    }

    @Test
    void getMidiNoteName() {
        assertEquals("C3", MidiMessage.getMidiNoteName(60, true, true, 3));
        assertEquals("--", MidiMessage.getMidiNoteName(129, true, true, 3));
    }

    @Test
    void createStatusByte() {
        assertEquals(0x90, MidiMessage.createStatusByte(0x90, 1), "Channel messages are Ok");
        assertNotEquals(0xF1, MidiMessage.createStatusByte(0xF1, 1), "System messages are not OK as they do not include a channel");
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
}