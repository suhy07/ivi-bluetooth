package com.jancar.bluetooth.broadcast;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jancar.bluetooth.viewmodels.DeviceViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suhy
 */
public class BluetoothPairReceiver extends BroadcastReceiver {
    private final static String TAG = "BluetoothPairReceiver";
    DeviceViewModel deviceViewModel;
    List<BluetoothDevice> bluetoothDevices;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
            if (deviceViewModel != null && bondState == BluetoothDevice.BOND_BONDED) {
                Log.d(TAG, "配对完成");
//                updateList(device);
            } else if (deviceViewModel != null && bondState == BluetoothDevice.BOND_NONE) {
                Log.d(TAG, "取消配对");
//                removeDevice(device);
            }
        }
    }

    public void setDeviceViewModel(DeviceViewModel deviceViewModel){
        this.deviceViewModel = deviceViewModel;
    }

    private void updateList(BluetoothDevice device){
        if (deviceViewModel.getDeviceList() != null
                && deviceViewModel.getDeviceList().getValue() != null) {
            bluetoothDevices = new ArrayList<>(deviceViewModel.getDeviceList().getValue());
        } else {
            bluetoothDevices = new ArrayList<>();
        }
        bluetoothDevices.remove(device);
        bluetoothDevices.add(0, device);
        deviceViewModel.setDeviceList(bluetoothDevices);
    }

    private void removeDevice(BluetoothDevice device) {
        if (deviceViewModel.getDeviceList() != null
                && deviceViewModel.getDeviceList().getValue() != null) {
            bluetoothDevices = new ArrayList<>(deviceViewModel.getDeviceList().getValue());
            bluetoothDevices.remove(device);
            bluetoothDevices.add(device);
        } else {
            bluetoothDevices = new ArrayList<>();
        }
        deviceViewModel.setDeviceList(bluetoothDevices);
    }
}

