package com.jancar.bluetooth.broadcast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.utils.BluetoothUtil;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suhy
 */
public class BluetoothScanReceiver extends BroadcastReceiver {
    private final static String TAG = "BluetoothScanReceiver";
    //每10条更新一次
    private final static int REFRESH_COUNT = 3;
    private static int count = 0;
    private DeviceViewModel deviceViewModel;
    private List<BluetoothDevice> bluetoothDeviceList =  new ArrayList<>();
    private boolean first = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        /*if(device != null) {
            Log.i(TAG, device.toString());
        } else {
            Log.i(TAG, "null");
        }*/
        switch (action) {
            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                Log.i(TAG, "扫描开始");
//                if (deviceViewModel != null && deviceViewModel.getDeviceSet() != null
//                        && deviceViewModel.getDeviceSet().getValue() != null) {
//                    bluetoothDeviceList = new HashSet<>(deviceViewModel.getDeviceSet().getValue());
//                } else {
//                    bluetoothDevices = new HashSet<>();
//                    bluetoothDevices.addAll(BluetoothUtil.getBondedDevices());
//                }
                /*if (deviceViewModel != null && deviceViewModel.getDeviceList() != null
                        && deviceViewModel.getDeviceList().getValue() != null) {
                    bluetoothDeviceList = new ArrayList<>(deviceViewModel.getDeviceList().getValue());
                } else {*/
                    //bluetoothDeviceList = new ArrayList<>();
                    bluetoothDeviceList.clear();
                    bluetoothDeviceList.addAll(BluetoothUtil.getBondedDevices());
                //}
                first = true;
                break;
            case BluetoothDevice.ACTION_FOUND:
                if(device != null){
                    Log.i(TAG, "发现设备:"+device.getName()+" "+device.getAddress());
                }
                if (deviceViewModel != null && device != null && bluetoothDeviceList != null &&
                    !bluetoothDeviceList.contains(device)) {
                    bluetoothDeviceList.add(device);
                    count++;
                    if (count == REFRESH_COUNT) {
                        count = 0;
                        deviceViewModel.setDeviceList(bluetoothDeviceList);
                    }
                }
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                Log.i(TAG, "扫描结束");
                /*if (Global.scanStatus == Global.SCANNING) {
                    BluetoothAdapter.getDefaultAdapter().startDiscovery();
                }*/
                if(deviceViewModel != null && first) {
                    first = false;
                    deviceViewModel.setDeviceList(bluetoothDeviceList);
                }
                break;
            default:
        }
    }

    public void setDeviceViewModel(DeviceViewModel deviceViewModel){
        this.deviceViewModel = deviceViewModel;
    }

}
