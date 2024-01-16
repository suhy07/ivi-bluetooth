package com.jancar.bluetooth.broadcast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.utils.BluetoothUtil;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author suhy
 */
public class BluetoothScanReceiver extends BroadcastReceiver {
    private final static String TAG = "BluetoothScanReceiver";
    //每10条更新一次
    private final static int REFRESH_COUNT = 2;
    private static int count = 0;
    private DeviceViewModel deviceViewModel;
    private Set<BluetoothDevice> bluetoothDevices;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if(device != null) {
            Log.i(TAG, device.toString());
        } else {
            Log.i(TAG, "null");
        }
        switch (action) {
            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                Log.i(TAG, "扫描开始");
                if (deviceViewModel != null && deviceViewModel.getDeviceSet() != null
                        && deviceViewModel.getDeviceSet().getValue() != null) {
                    bluetoothDevices = new HashSet<>(deviceViewModel.getDeviceSet().getValue());
                } else {
                    bluetoothDevices = new HashSet<>();
                    bluetoothDevices.addAll(BluetoothUtil.getBondedDevices());
                }
                break;
            case BluetoothDevice.ACTION_FOUND:
                Log.i(TAG, "发现设备");
                if (deviceViewModel != null && deviceViewModel.getDeviceSet() != null
                        && deviceViewModel.getDeviceSet().getValue() != null) {
                    bluetoothDevices = new HashSet<>(deviceViewModel.getDeviceSet().getValue());
                } else {
                    bluetoothDevices = new HashSet<>();
                    bluetoothDevices.addAll(BluetoothUtil.getBondedDevices());
                }
                if (deviceViewModel != null && device != null) {
                    bluetoothDevices.add(device);
                    count++;
                    if (count == REFRESH_COUNT) {
                        count = 0;
                        deviceViewModel.setDeviceSet(bluetoothDevices);
                    }
                }
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                Log.i(TAG, "扫描结束");
                if (Global.scanStatus == Global.SCANNING) {
                    BluetoothAdapter.getDefaultAdapter().startDiscovery();
                }
                if(deviceViewModel != null) {
                    deviceViewModel.setDeviceSet(bluetoothDevices);
                }
                break;
            default:
        }
    }

    public void setDeviceViewModel(DeviceViewModel deviceViewModel){
        this.deviceViewModel = deviceViewModel;
    }

}
