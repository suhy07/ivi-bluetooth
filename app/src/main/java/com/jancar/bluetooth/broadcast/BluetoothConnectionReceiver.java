package com.jancar.bluetooth.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.bluetooth.BluetoothDevice;
import android.os.RemoteException;
import android.util.Log;


import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;
import com.jancar.btservice.bluetooth.IBluetoothExecCallback;
import com.jancar.sdk.bluetooth.BluetoothManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author suhy
 */
public class BluetoothConnectionReceiver extends BroadcastReceiver {

    private final static String TAG = "BluetoothConnectionReceiver";
    private BluetoothManager bluetoothManager;
    private DeviceViewModel deviceViewModel;
    private Set<BluetoothDevice> deviceSet;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        deviceSet = new HashSet<>(deviceViewModel.getDeviceSet().getValue());
        bluetoothManager = MainApplication.getInstance().getBluetoothManager();
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            // 蓝牙设备已连接
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            deviceSet.remove(device);
            deviceSet.add(device);
            deviceViewModel.setDeviceSet(deviceSet);
            Log.d(TAG, "连接成功");
            // 处理已连接的设备
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            // 蓝牙设备已断开连接
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            deviceSet.remove(device);
            deviceSet.add(device);
            deviceViewModel.setDeviceSet(deviceSet);
            Log.d(TAG, "断开连接");
            // 处理已断开连接的设备
        }
    }

    public void setDeviceViewModel(DeviceViewModel deviceViewModel) {
        this.deviceViewModel = deviceViewModel;
    }
    private IBluetoothExecCallback.Stub stub = new IBluetoothExecCallback.Stub() {
        @Override
        public void onSuccess(String s) {

        }

        @Override
        public void onFailure(int i) {

        }
    };
}
