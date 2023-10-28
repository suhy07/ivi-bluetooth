/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.jancar.bluetooth.service;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.util.JsonReader;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.jancar.bluetooth.broadcast.BluetoothReceiver;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author suhy
 */

public class BluetoothScanService extends Service {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();
    private DeviceViewModel deviceViewModel;
    Set<android.bluetooth.BluetoothDevice> bluetoothDevices;

    public class LocalBinder extends Binder {
        public BluetoothScanService getService() {
            return BluetoothScanService.this;
        }
    }

    private final IBinder binder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        // 注册广播接收器来处理设备发现
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver, filter);
    }


    private void init() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //获取已配对的设备
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            //无权限
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                Log.d("device", device.getName() + device.getAddress());
                Log.d("null","" + (bluetoothDevices != null) + (deviceViewModel != null));
                if (deviceViewModel != null && bluetoothDevices != null) {
                    bluetoothDevices = deviceViewModel.getDeviceList().getValue();
                    bluetoothDevices.add(device);
                    deviceViewModel.setDeviceList(bluetoothDevices);
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setDeviceViewModel(DeviceViewModel deviceViewModel) {
        this.deviceViewModel = deviceViewModel;
        bluetoothDevices = deviceViewModel.getDeviceList().getValue();
        bluetoothReceiver.setDeviceViewModel(deviceViewModel);
    }

    public void startScan() {
        Log.d("start", "startScan");
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d("no","noPermission");
            return;
        }
        if (!bluetoothAdapter.isEnabled()){
           bluetoothAdapter.enable();
        }
        bluetoothAdapter.startDiscovery();
    }
    public void stopScan() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothReceiver);
    }

}
