####Midi4J - (Rt)Midi for java 
#####Overview
W.I.P. of a small Java [Midi](http://www.planetoftunes.com/midi-sequencing/midi-status-and-data-bytes.html) 
library bridged to a *(slightly revisited)*
[RtMidi](http://www.music.mcgill.ca/~gary/rtmidi/index.html)
cross platform C++ midi library using 
[JNA](https://github.com/java-native-access/jna) and 
[JnAerator](https://github.com/nativelibs4java/JNAerator). 

#####What is RtMidi?
It is a set of classes providing a common API (Application Programming Interface) for realtime 
MIDI input/output across Linux *(ALSA, JACK)*, Macintosh OS X *(CoreMIDI, JACK)*, and Windows *(Multimedia Library)* 
operating systems.

#####Huuuh! Another Java midi library... Why?
Well, it was done in view of future projects and as a fun exercise in my spare time to:
- Re-level-up my knowledge of the Midi protocol. 
- Learn:
    - How to create a small library with native binding *(my first one)*.
    - JNA and the *(sadly no longer developed)* JnAerator utility *(to bridge the C/C++ & Java worlds together)*.

- Discover:
    - The Alsa and Jack API.
    - Java development under Linux.
    - Junit, Exceptions, Javadoc, Markdown, UML etc...
    
![Midi4J Diagram](images/midi4j_class_diagram.png)

#####Philosophy:
One thing has tripped me quite a bit, at the beginning of using RtMidi*, querying devices/ports always results 
to available **opposite** (target) devices...

For example:
```javascript
// RtMidi C excerpt to get port count.
RtMidiIn *midiin = 0;
midiin = new RtMidiIn();

unsigned int nPorts = midiin->getPortCount();
```
returns the number of available **Midi Out** devices *(not available Midi In devices)* even if reading the code 
could make one think otherwise.

In Midi4J, to try to ease the possible confusion:
- Users created device instances are always known as **source** devices/ports
- All other devices/ports available on the system, are known as **target** devices/ports. 
```javascript
// Same as above in java with Midi4J naming convention.
boolean withUserCallback = false;
MidiIn midi4jIn = new MidiIn(withUserCallback);

int nPorts = midi4jIn.getTargetDeviceCount();
```
but also for other reasons such as:
- To conform to Java's own naming conventions.
- Trying to make method names and signatures self-explanatory.

therefore, the Midi4J's API is different from RtMidi.

#####Very simple example
Sends a D4 note on channel 1 for 1 second to IN target device and quit. 
More advanced samples are available in the 'Examples' package.
```javascript
import com.elemency.Midi4J.MidiMessage;
import com.elemency.Midi4J.MidiOut;
import com.elemency.Midi4J.RtMidiDriver.RtMidi;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        MidiOut midi4jOut = new MidiOut(RtMidi.Api.LINUX_ALSA.getIntValue(), "Midi4J");

        if (midi4jOut.getTargetDeviceCount() < 1) {
            System.out.println("No target device available - quitting...");
            return;
        }

        midi4jOut.connect("OUT", 0, true); // src Port name, tgt port ID, auto connect
        midi4jOut.sendMessage(MidiMessage.noteOn(1, 74, 100, 0)); // Channel, note, velocity, time stamp
        Thread.sleep(1000);
        midi4jOut.sendMessage(MidiMessage.noteOn(1, 74, 0, 0));
    }
}
```

#####Licensing
- Midi4J is licensed under the Apache license V2.0 except for the MidiMessage class which
incorporates work derived and translated from the JUCE library and licensed under the ISC License.

- RtMidi is distributed under its own modified MIT License.

Further details can be found in the LICENSE file.


#####Further Notes
Midi4J is still in its infancy and quite a few bugs are likely to be lurking around. 
Still, right now it seems to hold quite well with all I have thrown at it under the Jack and Alsa
Midi API, at least under Linux as I do not have the facility to test on Windows nor on macOs.

Thank you for your interest - have fun with it!


#####Addendum:
**(this likely applies also to most, if not all, other Sound/Midi libraries)*.

