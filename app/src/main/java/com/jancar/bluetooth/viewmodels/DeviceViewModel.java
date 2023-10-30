package com.jancar.bluetooth.viewmodels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.bluetooth.BluetoothDevice;

import java.util.HashSet;
import java.util.Set;

/**
 * @author suhy
 */
public class DeviceViewModel extends ViewModel {

    private MutableLiveData<Boolean> onOff;
    private MutableLiveData<Set<BluetoothDevice>> deviceList;
    private MutableLiveData<String> bluetoothName;

    public DeviceViewModel() {
        onOff = new MutableLiveData<>();
        deviceList = new MutableLiveData<>();
        deviceList.setValue(new HashSet<>());
        bluetoothName = new MutableLiveData<>();
    }



    public void setBluetoothName(String bluetoothName) {
        this.bluetoothName.setValue(bluetoothName);
    }

    public void setOnOff(boolean onOff){
        this.onOff.setValue(onOff);
    }

    public void setDeviceSet(Set<BluetoothDevice> devices) {
        deviceList.setValue(devices);
    }

    public LiveData<String> getBluetoothName() {
        return bluetoothName;
    }

    public LiveData<Boolean> getOnOff() {
        return onOff;
    }

    public LiveData<Set<BluetoothDevice>> getDeviceList() {
        return deviceList;
    }



}