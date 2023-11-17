package com.jancar.bluetooth.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.util.Log;
import android.widget.Toast;

import com.jancar.bluetooth.utils.BluetoothUtil;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;

import java.util.HashSet;

/**
 * @author suhy
 */
public class BluetoothStateReceiver extends BroadcastReceiver {
    private final static String TAG = "BluetoothStateReceiver";
    private DeviceViewModel deviceViewModel;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    // 蓝牙已关闭
                    deviceViewModel.setDeviceSet(new HashSet<>());
                    deviceViewModel.setOnOff(false);
                    Log.i(TAG, "蓝牙已关闭");
                    break;

                case BluetoothAdapter.STATE_ON:
                    deviceViewModel.setDeviceSet(BluetoothUtil.getBondedDevices());
                    deviceViewModel.setOnOff(true);
                    Log.i(TAG, "蓝牙已打开");
                    break;
            }
        }
    }

    public void setDeviceViewModel(DeviceViewModel deviceViewModel) {
        this.deviceViewModel = deviceViewModel;
    }

}
