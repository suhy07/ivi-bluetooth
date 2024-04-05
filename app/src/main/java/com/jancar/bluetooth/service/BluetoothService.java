/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.jancar.bluetooth.service;

import android.app.Service;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.bluetooth.BluetoothAdapter;

import com.jancar.bluetooth.broadcast.BluetoothConnectionReceiver;
import com.jancar.bluetooth.broadcast.BluetoothScanReceiver;
import com.jancar.bluetooth.broadcast.BluetoothPairReceiver;
import com.jancar.bluetooth.broadcast.BluetoothStateReceiver;
import com.jancar.bluetooth.ui.address.AddressViewModel;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;
import com.jancar.bluetooth.ui.music.MusicViewModel;
import com.jancar.bluetooth.viewmodels.PhoneViewModel;
import com.jancar.sdk.bluetooth.IVIBluetooth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author suhy
 */

public class BluetoothService extends Service {

    public static final String BT_CONNECTION_STATE_CHANGED = "android.bluetooth.headsetclient.profile.action.CONNECTION_STATE_CHANGED";

    private BluetoothAdapter bluetoothAdapter;
    private final BluetoothScanReceiver bluetoothScanReceiver = new BluetoothScanReceiver();
    private final BluetoothPairReceiver bluetoothPairReceiver = new BluetoothPairReceiver();
    private final BluetoothStateReceiver bluetoothStateReceiver = new BluetoothStateReceiver();
    private final BluetoothConnectionReceiver bluetoothConnectionReceiver = new BluetoothConnectionReceiver();
    private final static String TAG = "BluetoothService";
    private DeviceViewModel deviceViewModel;
    private AddressViewModel addressViewModel;
    private MusicViewModel musicViewModel;
    private PhoneViewModel phoneViewModel;

    public class LocalBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    private final IBinder binder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        // 注册广播接收器来处理设备发现
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothScanReceiver, filter);
        // 注册广播接收器来处理设备配对
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(bluetoothPairReceiver, filter1);
        // 监听蓝牙状态改变
        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateReceiver, filter2);
        // 监听连接状态
        IntentFilter filter3 = new IntentFilter();
        //filter3.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        //filter3.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter3.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        filter3.addAction(BluetoothA2dpSink.ACTION_CONNECTION_STATE_CHANGED);
        filter3.addAction(BluetoothHeadsetClient.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(bluetoothConnectionReceiver, filter3);
    }

    @Override
    public IBinder onBind(Intent intent) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return binder;
    }

    public void setDeviceViewModel(DeviceViewModel deviceViewModel) {
        this.deviceViewModel = deviceViewModel;
        bluetoothScanReceiver.setDeviceViewModel(deviceViewModel);
        bluetoothPairReceiver.setDeviceViewModel(deviceViewModel);
        bluetoothStateReceiver.setDeviceViewModel(deviceViewModel);
        bluetoothConnectionReceiver.setDeviceViewModel(deviceViewModel);
    }

    public void setAddressViewModel(AddressViewModel addressViewModel) {
        this.addressViewModel = addressViewModel;
        bluetoothConnectionReceiver.setAddressViewModel(addressViewModel);
        bluetoothStateReceiver.setAddressViewModel(addressViewModel);
    }

    public void setMusicViewModel(MusicViewModel musicViewModel) {
        this.musicViewModel = musicViewModel;
        bluetoothConnectionReceiver.setMusicViewModel(musicViewModel);
        bluetoothStateReceiver.setMusicViewModel(musicViewModel);
    }

    public void setPhoneViewModel(PhoneViewModel phoneViewModel) {
        this.phoneViewModel = phoneViewModel;
        bluetoothConnectionReceiver.setPhoneViewModel(phoneViewModel);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventPhoneStatus(IVIBluetooth.CallStatus event) {
        /*Log.i(TAG, event.toString());
        if(event.mStatus == IVIBluetooth.CallStatus.INCOMING ||
                event.mStatus == IVIBluetooth.CallStatus.OUTGOING) {
            boolean isComing = (event.mStatus == IVIBluetooth.CallStatus.INCOMING);
            String number = event.mPhoneNumber;
            String name = Global.findNameByNumber(number);
            Intent intent = new Intent(BluetoothService.this, CallActivity.class);
            intent.putExtra(Global.EXTRA_IS_COMING, isComing);
            intent.putExtra(Intent.EXTRA_PHONE_NUMBER, number);
            intent.putExtra(Global.EXTRA_NAME, name);
            startActivity(intent);
        }*/
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        unregisterReceiver(bluetoothScanReceiver);
        unregisterReceiver(bluetoothPairReceiver);
        unregisterReceiver(bluetoothStateReceiver);
        unregisterReceiver(bluetoothConnectionReceiver);
    }

}
