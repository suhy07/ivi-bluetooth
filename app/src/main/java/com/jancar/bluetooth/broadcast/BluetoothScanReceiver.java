package com.jancar.bluetooth.broadcast;

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
    DeviceViewModel deviceViewModel;
    Set<android.bluetooth.BluetoothDevice> bluetoothDevices;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("onReceive", "onReceive");
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // 从 intent 中获取发现的设备
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // 处理发现的设备，例如打印名称和地址
            if (deviceViewModel != null) {
                bluetoothDevices = new HashSet<>(deviceViewModel.getDeviceSet().getValue());
                bluetoothDevices.add(device);
                deviceViewModel.setDeviceSet(bluetoothDevices);
            }
        }
    }

    public void setDeviceViewModel(DeviceViewModel deviceViewModel){
        this.deviceViewModel = deviceViewModel;
    }

}
