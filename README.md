####Midi4J - (Rt)Midi for java 
W.I.P. of a small Java [Midi](http://www.planetoftunes.com/midi-sequencing/midi-status-and-data-bytes.html) 
library bridged to the excellent but slightly modified 
[RtMidi](https://github.com/thestk/rtmidi) 
multi platform native library using 
[JNA](https://github.com/java-native-access/jna) and 
[JnAerator](https://github.com/nativelibs4java/JNAerator). 

This has been done as I needed a tiny library for future projects but also as a fun exercise in my spare time to:
- Re level my knowledge of the Midi protocol. 
- Learn how to:
    - Create a small library (my first one)
    - Use JNA and the amazing (sadly not developed anymore) JnAerator 
      Jna utility (to bridge the C/C++ & Java worlds together)
    - Junit, Exceptions, Javadoc, Markdown etc...
    
One important thing to be aware (which, I must admit, has tripped me for quite a while) is that listing devices/ports 
always returns the list of the available opposite device type (i.e Midi4J.MidiIn.listTargetDevices() returns all 
available Midi OUT devices and vice versa).
To try to ease this notion, a Midi4J.MidiIn and/or a Midi4J.MidiOut device instance is always known as a source 
device/port whilst all other devices/ports available on the system, are known as target devices/ports. 
This is reflected in the name of every device methods, fields and variables of the MidiDeviceMgr class.

It is still very rough and I am sure there are quite a few bugs lurking around. Right now it seems to 
hold quite well with all the simple things I have thrown at it under the Jack and Alsa Midi API, at least under linux 
(I do not have the facility to test on Windows nor on Mac).

Anyway thank you for your interest - have fun with it!
