package com.jancar.bluetooth.model;

/**
 * @author suhy
 */
public class CallLog {
    private String callName;
    private String callTime;
    private String callNumber;

    public CallLog (String callName, String callTime, String callNumber) {
        this.callName = callName;
        this.callTime = callTime;
        this.callNumber = callNumber;
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

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

}
