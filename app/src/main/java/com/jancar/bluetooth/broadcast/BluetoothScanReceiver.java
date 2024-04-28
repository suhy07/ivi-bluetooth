package com.jancar.bluetooth.broadcast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.ui.device.DeviceViewModel;
import com.jancar.bluetooth.utils.BluetoothUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import me.goldze.mvvmhabit.bus.Messenger;

/**
 * @author suhy
 */
public class BluetoothScanReceiver extends BroadcastReceiver {
    private final static String TAG = "BluetoothScanReceiver";
    //每10条更新一次
    private final static int REFRESH_COUNT = 3;
    private static int count = 0;
    private List<BluetoothDevice> bluetoothDeviceList = new ArrayList<>();
    private boolean first = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        switch (action) {
            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                Log.i(TAG, "扫描开始");

                break;
            case BluetoothDevice.ACTION_FOUND:
                if(device != null){
                    Log.i(TAG, "发现设备:"+device.getName()+" "+device.getAddress());
                }
                Messenger.getDefault().send(device, Global.Tokens.TOKEN_DEVICEVIEWMODEL_ADDDEVICE);
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                Log.i(TAG, "扫描结束");
//                setDeviceList(bluetoothDeviceList);
                break;
            default:
        }
    }
}
