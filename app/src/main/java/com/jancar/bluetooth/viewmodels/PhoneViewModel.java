package com.jancar.bluetooth.viewmodels;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

/**
 * @author suhy
 */
public class PhoneViewModel extends ViewModel {

    private final MutableLiveData<String> callNumber;
    private final MutableLiveData<String> callName;

    public PhoneViewModel() {
        this.callNumber = new MutableLiveData<>();
        this.callNumber.setValue("");
        this.callName = new MutableLiveData<>();
        this.callName.setValue("");
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
}