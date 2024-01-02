package com.jancar.bluetooth.viewmodels;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

/**
 * @author suhy
 */
public class PhoneViewModel extends ViewModel {

    private final MutableLiveData<String> callNumber;
    private final MutableLiveData<String> callName;

    public MutableLiveData<Boolean> getConnectStatus() {
        return connectStatus;
    }

    public void setConnectStatus(boolean isConnected) {
        this.connectStatus.setValue(isConnected);
    }

    private  MutableLiveData<Boolean> connectStatus;

    public PhoneViewModel() {
        this.connectStatus = new MutableLiveData<>();
        this.callNumber = new MutableLiveData<>();
        this.callNumber.setValue("");
        this.callName = new MutableLiveData<>();
        this.callName.setValue("");

    }

    public MutableLiveData<String> getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String str) {
        Log.i("liyongde","setCallNumber:"+str);
        this.callNumber.setValue(str);
    }

    public MutableLiveData<String> getCallName() {
        return callName;
    }

    public void setCallName(String str) {
        this.callName.setValue(str);
    }
}