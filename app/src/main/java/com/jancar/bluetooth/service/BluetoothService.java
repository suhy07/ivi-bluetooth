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
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanSettings;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.jancar.bluetooth.broadcast.BluetoothScanReceiver;
import com.jancar.bluetooth.broadcast.BluetoothPairReceiver;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;

import java.util.Set;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * @author suhy
 */
@AndroidEntryPoint
public class BluetoothService extends Service {
    private BluetoothAdapter bluetoothAdapter;
    private final BluetoothScanReceiver bluetoothConnectReceiver = new BluetoothScanReceiver();
    private final BluetoothPairReceiver bluetoothPairReceiver = new BluetoothPairReceiver();
    public DeviceViewModel deviceViewModel;
    Set<android.bluetooth.BluetoothDevice> bluetoothDevices;

    public class LocalBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    private final IBinder binder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        // 注册广播接收器来处理设备发现
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothConnectReceiver, filter);
//        // 注册广播接收器来处理设备配对
//        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//        registerReceiver(bluetoothPairReceiver, filter1);
    }

    @Override
    public IBinder onBind(Intent intent) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return binder;
    }

    public void setDeviceViewModel(DeviceViewModel deviceViewModel) {
        this.deviceViewModel = deviceViewModel;
        bluetoothConnectReceiver.setDeviceViewModel(deviceViewModel);
        bluetoothPairReceiver.setDeviceViewModel(deviceViewModel);
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
        unregisterReceiver(bluetoothConnectReceiver);
        unregisterReceiver(bluetoothPairReceiver);
    }

}
