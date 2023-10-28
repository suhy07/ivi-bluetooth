/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.jancar.bluetooth.broadcast;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.jancar.bluetooth.viewmodels.DeviceViewModel;

import java.util.List;
import java.util.Set;

/**
 * @author suhy
 */
public class BluetoothReceiver extends BroadcastReceiver {
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
            String deviceName = device.getName();
            String deviceAddress = device.getAddress();
            Log.d("device", device.getName() + device.getAddress());
            if (deviceViewModel != null) {
                bluetoothDevices = deviceViewModel.getDeviceList().getValue();
                bluetoothDevices.add(device);
                deviceViewModel.setDeviceList(bluetoothDevices);
            }
        }
    }

    public void setDeviceViewModel(DeviceViewModel deviceViewModel){
        this.deviceViewModel = deviceViewModel;
    }

}
