package com.jancar.bluetooth.viewmodels;

;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.bluetooth.BluetoothDevice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author suhy
 */
public class DeviceViewModel extends ViewModel {

    private MutableLiveData<Boolean> onOff;
    private MutableLiveData<Set<BluetoothDevice>> deviceSet;
    private MutableLiveData<String> bluetoothName;

    public DeviceViewModel() {
        onOff = new MutableLiveData<>();
        deviceSet = new MutableLiveData<>();
        deviceSet.setValue(new HashSet<>());
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

    public void setDeviceSet(List<BluetoothDevice> devices) {
        setDeviceSet(new HashSet<>(devices));
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

}