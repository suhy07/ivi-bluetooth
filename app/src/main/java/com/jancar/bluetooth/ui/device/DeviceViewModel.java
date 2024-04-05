package com.jancar.bluetooth.ui.device;

import android.annotation.NonNull;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * @author suhy
 */
public class DeviceViewModel extends BaseViewModel {

    private MutableLiveData<Boolean> onOff;
    private MutableLiveData<List<BluetoothDevice>> deviceList;
    private MutableLiveData<String> bluetoothName;

    public DeviceViewModel(@NonNull Application application) {
        super(application);
        onOff = new MutableLiveData<>();
        deviceList = new MutableLiveData<>();
        deviceList.setValue(new ArrayList<>());
        bluetoothName = new MutableLiveData<>();
    }

    public void setBluetoothName(String bluetoothName) {
        this.bluetoothName.setValue(bluetoothName);
    }

    public void setOnOff(boolean onOff){
        this.onOff.setValue(onOff);
    }

    public void setDeviceList(List<BluetoothDevice> devices) {
        deviceList.setValue(devices);
    }

    public LiveData<String> getBluetoothName() {
        return bluetoothName;
    }

    public LiveData<Boolean> getOnOff() {
        return onOff;
    }

    public MutableLiveData<List<BluetoothDevice>> getDeviceList() {
        return deviceList;
    }
}