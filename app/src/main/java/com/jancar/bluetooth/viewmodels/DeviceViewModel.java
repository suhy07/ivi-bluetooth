package com.jancar.bluetooth.viewmodels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.bluetooth.BluetoothDevice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author suhy
 */
public class DeviceViewModel extends ViewModel {

    private MutableLiveData<Boolean> onOff;
    private MutableLiveData<Set<BluetoothDevice>> deviceSet;
    private MutableLiveData<Map<String, Boolean>> connStatus;
    private MutableLiveData<String> bluetoothName;

    public DeviceViewModel() {
        onOff = new MutableLiveData<>();
        deviceSet = new MutableLiveData<>();
        deviceSet.setValue(new HashSet<>());
        connStatus = new MutableLiveData<>();
        connStatus.setValue(new HashMap<>());
        bluetoothName = new MutableLiveData<>();
    }



    public void setBluetoothName(String bluetoothName) {
        this.bluetoothName.setValue(bluetoothName);
    }

    public void setOnOff(boolean onOff){
        this.onOff.setValue(onOff);
    }

    public void setDeviceSet(Set<BluetoothDevice> devices) {
        deviceSet.setValue(devices);
    }

    public void setConnStatus(Map<String, Boolean> connStatus) {
        this.connStatus.setValue(connStatus);
    }

    public LiveData<String> getBluetoothName() {
        return bluetoothName;
    }

    public LiveData<Boolean> getOnOff() {
        return onOff;
    }

    public LiveData<Set<BluetoothDevice>> getDeviceSet() {
        return deviceSet;
    }

    public LiveData<Map<String, Boolean>> getConnStatus() {
        return connStatus;
    }

}