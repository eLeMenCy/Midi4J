package com.elemency.VertxRtMidi;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

/**
 * Wraps an RtMidi object for C function return statuses.<br>
 * <i>native declaration : /run/media/elemency/Data/Prjs/SandBox/Midi/RtMidi/rtmidi_c.h:40</i><br>
 */
public class RtMidiWrapper extends Structure {
	/** The wrapped RtMidi object. (C type : void*) */
	public Pointer ptr;
	/** C type : void* */
	public Pointer data;

	/** True when the last function call was OK. */
	public byte ok;

	/** If an error occured (ok != true), set to an error message. (C type : const char*) */
	public Pointer msg;

	public RtMidiWrapper() {
		super();
	}
	protected List<? > getFieldOrder() {
		return Arrays.asList("ptr", "data", "ok", "msg");
	}
	/**
	 * @param ptr C type : void*<br>
	 * @param data C type : void*<br>
	 * @param msg C type : const char*
	 */
	public RtMidiWrapper(Pointer ptr, Pointer data, byte ok, Pointer msg) {
		super();
		this.ptr = ptr;
		this.data = data;
		this.ok = ok;
		this.msg = msg;
	}

	public RtMidiWrapper(Pointer peer) {
		super(peer);
	}

	public static class ByReference extends RtMidiWrapper implements Structure.ByReference {
		
	}

	public static class ByValue extends RtMidiWrapper implements Structure.ByValue {
		
	}
}
