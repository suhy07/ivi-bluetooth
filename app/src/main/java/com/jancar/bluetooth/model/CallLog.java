package com.jancar.bluetooth.model;

/**
 * @author suhy
 */
public class CallLog {
    private String callName;
    private String callTime;

    public CallLog (String callName, String callTime) {
        this.callName = callName;
        this.callTime = callTime;
    }

    public String getCallName() {
        return callName;
    }

    public void setCallName(String callName) {
        this.callName = callName;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }
}
