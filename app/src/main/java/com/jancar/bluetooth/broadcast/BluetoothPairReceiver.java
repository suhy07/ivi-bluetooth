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
public class BluetoothPairReceiver extends BroadcastReceiver {
    private final static String TAG = "BluetoothPairReceiver";
    DeviceViewModel deviceViewModel;
    Set<BluetoothDevice> bluetoothDevices;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
            if (deviceViewModel != null && bondState == BluetoothDevice.BOND_BONDED) {
                Log.d(TAG, "配对完成");
                bluetoothDevices = new HashSet<>(deviceViewModel.getDeviceSet().getValue());
                bluetoothDevices.add(device);
                deviceViewModel.setDeviceSet(bluetoothDevices);
            } else if (deviceViewModel != null && bondState == BluetoothDevice.BOND_NONE) {
                Log.d(TAG, "取消配对");
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

