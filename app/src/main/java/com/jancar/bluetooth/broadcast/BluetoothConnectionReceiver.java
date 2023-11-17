package com.jancar.bluetooth.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.bluetooth.BluetoothDevice;
import android.util.Log;


import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.viewmodels.AddressViewModel;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;
import com.jancar.bluetooth.viewmodels.MusicViewModel;
import com.jancar.btservice.bluetooth.IBluetoothExecCallback;
import com.jancar.sdk.bluetooth.BluetoothManager;
import com.jancar.sdk.bluetooth.IVIBluetooth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author suhy
 */
public class BluetoothConnectionReceiver extends BroadcastReceiver {

    private final static String TAG = "BluetoothConnectionReceiver";
    private BluetoothManager bluetoothManager;
    private DeviceViewModel deviceViewModel;
    private AddressViewModel addressViewModel;
    private MusicViewModel musicViewModel;
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
            bluetoothManager.connect();
            bluetoothManager.openBluetoothModule(null);
            // 处理已连接的设备
            Global.connStatus = Global.CONNECTED;
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            // 蓝牙设备已断开连接
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            deviceSet.remove(device);
            deviceSet.add(device);
            deviceViewModel.setDeviceSet(deviceSet);
            Log.d(TAG, "断开连接");
            // 处理已断开连接的设备
            bluetoothManager.stopContactOrHistoryLoad(null);
            Global.setContactList(new ArrayList<>());
            addressViewModel.setCallLogList(new ArrayList<>());
            addressViewModel.setContactList(new ArrayList<>());
            musicViewModel.setMusicName("");
            musicViewModel.setArtist("");
            musicViewModel.setA2dpStatus(IVIBluetooth.BluetoothA2DPStatus.READY);
            Global.connStatus = Global.NOT_CONNECTED;
        }
    }

    public void setDeviceViewModel(DeviceViewModel deviceViewModel) {
        this.deviceViewModel = deviceViewModel;
    }

    public void setAddressViewModel(AddressViewModel addressViewModel) {
        this.addressViewModel = addressViewModel;
    }

    public void setMusicViewModel(MusicViewModel musicViewModel) {
        this.musicViewModel = musicViewModel;
    }

}
