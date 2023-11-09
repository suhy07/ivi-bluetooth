package com.jancar.bluetooth.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class DialViewModel extends ViewModel {

    private final MutableLiveData<String> callNumber;
    private final MutableLiveData<String> callName;
    private final MutableLiveData<String> etNum;

    public DialViewModel() {
        this.callNumber = new MutableLiveData<>();
        this.callNumber.setValue("");
        this.callName = new MutableLiveData<>();
        this.callName.setValue("");
        this.etNum = new MutableLiveData<>();
        this.etNum.setValue("");
    }

    public MutableLiveData<String> getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String str) {
        this.callNumber.setValue(str);
    }

    public MutableLiveData<String> getCallName() {
        return callName;
    }

    public void setCallName(String str) {
        this.callName.setValue(str);
    }

    public MutableLiveData<String> getEtNum() {
        return etNum;
    }

    public void setEtNum(String str) {
        this.etNum.setValue(str);
    }
}
