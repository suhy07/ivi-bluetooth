package com.jancar.bluetooth.ui.phone;


import android.annotation.NonNull;
import android.app.Application;
import android.arch.lifecycle.MutableLiveData;

import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * @author suhy
 */
public class PhoneViewModel extends BaseViewModel {

    private final MutableLiveData<String> callNumber;
    private final MutableLiveData<String> callName;

    public MutableLiveData<Boolean> getConnectStatus() {
        return connectStatus;
    }

    public void setConnectStatus(boolean isConnected) {
        this.connectStatus.setValue(isConnected);
    }

    private  MutableLiveData<Boolean> connectStatus;

    public PhoneViewModel(@NonNull Application application) {
        super(application);
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