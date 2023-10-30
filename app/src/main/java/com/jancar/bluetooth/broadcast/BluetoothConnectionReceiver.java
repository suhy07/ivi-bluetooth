package com.jancar.bluetooth.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import androidx.constraintlayout.helper.widget.Layer;

import com.jancar.bluetooth.viewmodels.DeviceViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author suhy
 */
public class BluetoothConnectionReceiver extends BroadcastReceiver {

    DeviceViewModel deviceViewModel;
    Map<String, Boolean> conn;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        conn = new HashMap<>(deviceViewModel.getConnStatus().getValue());
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            // 蓝牙设备已连接
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String deviceAddress = device.getAddress();
            conn.put(deviceAddress, true);
            Log.d("?!", "连接成功");
            // 处理已连接的设备
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            // 蓝牙设备已断开连接
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String deviceAddress = device.getAddress();
            conn.put(deviceAddress, false);
            Log.d("?!", "断开连接");
            // 处理已断开连接的设备
        }
        deviceViewModel.setConnStatus(conn);
    }

    public void setDeviceViewModel(DeviceViewModel deviceViewModel) {
        this.deviceViewModel = deviceViewModel;
    }

}
