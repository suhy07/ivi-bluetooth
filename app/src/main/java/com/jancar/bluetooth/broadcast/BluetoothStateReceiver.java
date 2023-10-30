package com.jancar.bluetooth.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.widget.Toast;

import com.jancar.bluetooth.utils.BluetoothUtil;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;

import java.util.HashSet;

/**
 * @author suhy
 */
public class BluetoothStateReceiver extends BroadcastReceiver {
    DeviceViewModel deviceViewModel;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    // 蓝牙已关闭
                    deviceViewModel.setDeviceSet(new HashSet<>());
                    Toast.makeText(context, "蓝牙已关闭", Toast.LENGTH_SHORT).show();
                    break;

                case BluetoothAdapter.STATE_ON:
                    deviceViewModel.setDeviceSet(BluetoothUtil.getBondedDevices());
                    Toast.makeText(context, "蓝牙已打开", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public void setDeviceViewModel(DeviceViewModel deviceViewModel) {
        this.deviceViewModel = deviceViewModel;
    }

}
