####Midi4J - (Rt)Midi for java 
W.I.P. of a small Java [Midi](http://www.planetoftunes.com/midi-sequencing/midi-status-and-data-bytes.html) 
library bridged to the excellent
[RtMidi](https://github.com/thestk/rtmidi) 
multi platform native midi library (slightly revisited) using 
[JNA](https://github.com/java-native-access/jna) and 
[JnAerator](https://github.com/nativelibs4java/JNAerator). 

This has been done as I needed a tiny library for future projects but also as a fun exercise in my spare time to:
- Re-level-up my knowledge of the Midi protocol. 
- Learn how to:
    - Create a small library (my first one).
    - Use JNA and the amazing (sadly not developed anymore) JnAerator.
      Jna utility (to bridge the C/C++ & Java worlds together).
- Discover:
    - Alsa and Jack API.
    - Java development under Linux.
    - Junit, Exceptions, Javadoc, Markdown etc...
    
One important thing to be aware of when listing devices/ports under RtMidi 
(which, I must admit, has tripped me for quite a while at the beginning) 
is that it always returns the list of available opposite (target) devices. For example:
```javascript
    // RtMidi C excerpt to get port count.
    RtMidiIn *midiin = 0;
    midiin = new RtMidiIn();

    unsigned int nPorts = midiin->getPortCount();
```
returns the number of available Midi Out devices (not available Midi In devices) even if reading the code 
could make one think otherwise.

To try to ease this notion, a Midi4J.MidiIn and/or a Midi4J.MidiOut device instance is always known as a source 
device/port whilst all other devices/ports available on the system, are known as target devices/ports. 
This is reflected in Midi4J's name and signature of every device methods, fields and variables.
```javascript
    // Same as above in java with Midi4J naming convention.
    boolean withUserCallback = false;
    MidiIn midi4jIn = new MidiIn(withUserCallback);

    int nPorts = midi4jIn.getTargetDeviceCount();
```

It is still very rough and I am sure there are quite a few bugs lurking around. Right now it seems to 
hold quite well with all the simple things I have thrown at it under the Jack and Alsa Midi API, at least under Linux 
as I do not have the facility to test on Windows nor on Mac.

Regarding the license, it will likely be one of the 3 permissive licenses below:
- Apache License 2.0
- BSD License
- ISC license.

Anyway thank you for your interest - have fun with it!
