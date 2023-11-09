package com.jancar.bluetooth.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class CallViewModel extends ViewModel {

    private MutableLiveData<Boolean> isComing;
    private MutableLiveData<String> number;
    private MutableLiveData<String> name;

    public CallViewModel() {
        isComing = new MutableLiveData<>();
        isComing.setValue(false);
        number = new MutableLiveData<>();
        number.setValue("");
        name = new MutableLiveData<>();
        name.setValue("");
    }

    public MutableLiveData<Boolean> getIsComing() {
        return isComing;
    }

    public void setIsComing(boolean isComing) {
        this.isComing.setValue(isComing);
    }

    public MutableLiveData<String> getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number.setValue(number);
    }

    public MutableLiveData<String> getName() {
        return name;
    }

    public void setName(String name) {
        this.name.setValue(name);
    }
}
