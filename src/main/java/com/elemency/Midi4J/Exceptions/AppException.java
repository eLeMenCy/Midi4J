package com.elemency.Midi4J.Exceptions;

import javax.sound.midi.MidiMessage;
import java.util.ArrayList;
import java.util.List;

public class AppException extends RuntimeException {
    protected List<ErrorInfo> errorInfoList = new ArrayList<>();

    public AppException() {
    }

    public ErrorInfo addInfo(ErrorInfo info) {
        this.errorInfoList.add(info);
        return info;
    }

    public ErrorInfo addInfo() {
        ErrorInfo info = new ErrorInfo();
        this.errorInfoList.add(info);
        return info;
    }

    public List<ErrorInfo> getErrorInfoList() {
        return errorInfoList;
    }
}
