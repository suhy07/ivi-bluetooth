/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.jancar.bluetooth.service;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanSettings;
import android.util.Log;

import com.jancar.bluetooth.broadcast.BluetoothConnectionReceiver;
import com.jancar.bluetooth.broadcast.BluetoothScanReceiver;
import com.jancar.bluetooth.broadcast.BluetoothPairReceiver;
import com.jancar.bluetooth.broadcast.BluetoothStateReceiver;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;

/**
 * @author suhy
 */

public class BluetoothService extends Service {
    private BluetoothAdapter bluetoothAdapter;
    private final BluetoothScanReceiver bluetoothScanReceiver = new BluetoothScanReceiver();
    private final BluetoothPairReceiver bluetoothPairReceiver = new BluetoothPairReceiver();
    private final BluetoothStateReceiver bluetoothStateReceiver = new BluetoothStateReceiver();
    private final BluetoothConnectionReceiver bluetoothConnectionReceiver = new BluetoothConnectionReceiver();
    public DeviceViewModel deviceViewModel;

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
        registerReceiver(bluetoothScanReceiver, filter);
        // 注册广播接收器来处理设备配对
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(bluetoothPairReceiver, filter1);
        // 监听蓝牙状态改变
        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateReceiver, filter2);
        // 监听连接状态
        IntentFilter filter3 = new IntentFilter();
        filter3.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter3.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(bluetoothConnectionReceiver, filter3);
    }

    @Override
    public IBinder onBind(Intent intent) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return binder;
    }

    public void setDeviceViewModel(DeviceViewModel deviceViewModel) {
        this.deviceViewModel = deviceViewModel;
        bluetoothScanReceiver.setDeviceViewModel(deviceViewModel);
        bluetoothPairReceiver.setDeviceViewModel(deviceViewModel);
        bluetoothStateReceiver.setDeviceViewModel(deviceViewModel);
        bluetoothConnectionReceiver.setDeviceViewModel(deviceViewModel);
    }

    public void startScan() {
        Log.d("start", "startScan");
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        if (!bluetoothAdapter.isEnabled()){
           bluetoothAdapter.enable();
        }
        bluetoothAdapter.startDiscovery();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothScanReceiver);
        unregisterReceiver(bluetoothPairReceiver);
        unregisterReceiver(bluetoothStateReceiver);
        unregisterReceiver(bluetoothConnectionReceiver);
    }

}
