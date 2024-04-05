package com.jancar.bluetooth.ui.phone;


import android.annotation.NonNull;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.jancar.bluetooth.ui.main.MainItemViewModel;
import com.jancar.bluetooth.ui.main.MainViewModel;

/**
 * @author suhy
 */
public class PhoneViewModel extends MainItemViewModel {

    private final MutableLiveData<String> callNumber;
    private final MutableLiveData<String> callName;

    public MutableLiveData<Boolean> getConnectStatus() {
        return connectStatus;
    }

    public void setConnectStatus(boolean isConnected) {
        this.connectStatus.setValue(isConnected);
    }

    private  MutableLiveData<Boolean> connectStatus;

    public PhoneViewModel(@NonNull MainViewModel mainViewModel) {
        super(mainViewModel);
        this.connectStatus = new MutableLiveData<>();
        this.callNumber = new MutableLiveData<>();
        this.callNumber.setValue("");
        this.callName = new MutableLiveData<>();
        this.callName.setValue("");

    }

    /*public MutableLiveData<String> getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String str) {
        this.callNumber.setValue(str);
    }

    public MutableLiveData<String> getCallName() {
        return callName;
    }*/

    public void setCallName(String str) {
        this.callName.setValue(str);
    }
}