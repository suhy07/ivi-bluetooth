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
    DeviceViewModel deviceViewModel;
    Set<BluetoothDevice> bluetoothDevices;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("onReceive", "onReceive");
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
            if (bondState == BluetoothDevice.BOND_BONDED) {
                // 配对完成
                bluetoothDevices = new HashSet<>(deviceViewModel.getDeviceList().getValue());
                bluetoothDevices.add(device);
                deviceViewModel.setDeviceSet(bluetoothDevices);
            }
        }
    }

    public void setDeviceViewModel(DeviceViewModel deviceViewModel){
        this.deviceViewModel = deviceViewModel;
    }
}

