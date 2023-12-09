package com.jancar.bluetooth.broadcast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jancar.bluetooth.viewmodels.DeviceViewModel;

import java.util.HashSet;
import java.util.Set;

/**
 * @author suhy
 */
public class BluetoothScanReceiver extends BroadcastReceiver {
    private final static String TAG = "BluetoothScanReceiver";
    //每10条更新一次
    private final static int REFRESH_COUNT = 10;
    private static int count = 0;
    private DeviceViewModel deviceViewModel;
    private Set<BluetoothDevice> bluetoothDevices;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        switch (action) {
            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                if (deviceViewModel != null && deviceViewModel.getDeviceSet() != null) {
                    bluetoothDevices = new HashSet<>(deviceViewModel.getDeviceSet().getValue());
                } else {
                    bluetoothDevices = new HashSet<>();
                }
                break;
            case BluetoothDevice.ACTION_FOUND:
                if (deviceViewModel != null && device != null && bluetoothDevices != null) {
                    bluetoothDevices.add(device);
                    count++;
                    if (count == REFRESH_COUNT) {
                        count = 0;
                        deviceViewModel.setDeviceSet(bluetoothDevices);
                    }
                }
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                if(deviceViewModel != null) {
                    deviceViewModel.setDeviceSet(bluetoothDevices);
                }
                break;
        }
    }

    public void setDeviceViewModel(DeviceViewModel deviceViewModel){
        this.deviceViewModel = deviceViewModel;
    }

}
