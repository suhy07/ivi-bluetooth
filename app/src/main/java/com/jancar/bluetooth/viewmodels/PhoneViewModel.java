package com.jancar.bluetooth.viewmodels;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

/**
 * @author suhy
 */
public class PhoneViewModel extends ViewModel {


    private final MutableLiveData<String> callNumber;

    public PhoneViewModel() {
        this.callNumber = new MutableLiveData<>();
        this.callNumber.setValue("");
    }

    public MutableLiveData<String> getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String str) {
        this.callNumber.setValue(str);
    }

}