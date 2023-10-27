package com.jancar.bluetooth.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author suhy
 */
public class DeviceViewModel extends ViewModel {

    private final MutableLiveData<Boolean> mOnOff;

    public DeviceViewModel() {
        mOnOff = new MutableLiveData<>();
    }

    public void setOnOff(boolean onOff){
        mOnOff.setValue(onOff);
    }

    public LiveData<Boolean> getOnOff() {
        return mOnOff;
    }

}