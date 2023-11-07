package com.jancar.bluetooth.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.bluetooth.BluetoothDevice;
import android.util.Log;


import com.jancar.bluetooth.viewmodels.DeviceViewModel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author suhy
 */
public class BluetoothConnectionReceiver extends BroadcastReceiver {

    private final static String TAG = "BluetoothConnectionReceiver";
    private DeviceViewModel deviceViewModel;
    private Map<String, Boolean> conn;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        conn = new HashMap<>(deviceViewModel.getConnStatus().getValue());
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            // 蓝牙设备已连接
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String deviceAddress = device.getAddress();
            conn.put(deviceAddress, true);
            Log.d(TAG, "连接成功");
            // 处理已连接的设备
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            // 蓝牙设备已断开连接
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String deviceAddress = device.getAddress();
            conn.put(deviceAddress, false);
            Log.d(TAG, "断开连接");
            // 处理已断开连接的设备
        }
        deviceViewModel.setConnStatus(conn);
    }

    public void setDeviceViewModel(DeviceViewModel deviceViewModel) {
        this.deviceViewModel = deviceViewModel;
    }

}
