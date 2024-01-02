package com.jancar.bluetooth.broadcast;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.bluetooth.BluetoothDevice;
import android.util.Log;


import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.service.BluetoothService;
import com.jancar.bluetooth.utils.BluetoothUtil;
import com.jancar.bluetooth.utils.CallUtil;
import com.jancar.bluetooth.viewmodels.AddressViewModel;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;
import com.jancar.bluetooth.viewmodels.MusicViewModel;
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
        Log.i("liyongde","onReceive:"+action);
        if (deviceViewModel != null && deviceViewModel.getDeviceSet() != null
        && deviceViewModel.getDeviceSet().getValue() != null) {
            deviceSet = new HashSet<>(deviceViewModel.getDeviceSet().getValue());
        } else {
            deviceSet = new HashSet<>();
        }
        bluetoothManager = MainApplication.getInstance().getBluetoothManager();
        boolean isHfpAction = BluetoothHeadsetClient.ACTION_CONNECTION_STATE_CHANGED.equals(action);
        boolean isA2dpAction = BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED.equals(action);
        boolean isA2dpSinkAction = BluetoothA2dpSink.ACTION_CONNECTION_STATE_CHANGED.equals(action);
        if(isHfpAction|| isA2dpAction || isA2dpSinkAction){
            int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
            BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.i("liyongde",device.getName()+" "+device.getAddress()+" "+state+" "+device.isConnected());
            if(isHfpAction){
                CallUtil.getInstance().setHfpStatus(state);
            }
            if(isA2dpAction){
                CallUtil.getInstance().setA2dpStatus(state);
            }
            if(isA2dpSinkAction){
                CallUtil.getInstance().setA2dpSinkStatus(state);
            }

            if(device!=null){
                if(state == BluetoothProfile.STATE_DISCONNECTED){
                    if(isHfpAction){
                        CallUtil.getInstance().setConnectingHfpMac("");
                    }
                    if(isA2dpAction||isA2dpSinkAction){
                        CallUtil.getInstance().setConnectingA2dpMac("");
                    }

                    // 蓝牙设备已断开连接

                    deviceSet.remove(device);

                    BluetoothDevice newDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device.getAddress());

                    if(newDevice!=null){
                        deviceSet.add(newDevice);
                    }

                    if (deviceViewModel != null) {
                        deviceViewModel.setDeviceSet(deviceSet);
                    }
                    Log.d(TAG, "断开连接");
                    // 处理已断开连接的设备
//            bluetoothManager.stopContactOrHistoryLoad(null);
                    Global.setContactList(new ArrayList<>());
                    if (addressViewModel != null) {
                        addressViewModel.setCallLogList(new ArrayList<>());
                        addressViewModel.setContactList(new ArrayList<>());
                    }
                    if (musicViewModel != null) {
                        musicViewModel.setMusicName("");
                        musicViewModel.setArtist("");
                        musicViewModel.setA2dpStatus(IVIBluetooth.BluetoothA2DPStatus.READY);
                    }
                    Global.connStatus = Global.NOT_CONNECTED;

                }else if(state == BluetoothProfile.STATE_CONNECTING){
                    if(isHfpAction){
                        CallUtil.getInstance().setConnectingHfpMac(device.getAddress());
                    }
                    if(isA2dpAction||isA2dpSinkAction){
                        CallUtil.getInstance().setConnectingA2dpMac(device.getAddress());
                    }
                    deviceSet.remove(device);
                    deviceSet.add(device);
                    if (deviceViewModel != null) {
                        deviceViewModel.setDeviceSet(deviceSet);
                    }
                }else if(state == BluetoothProfile.STATE_CONNECTED){
                    if(isHfpAction){
                       CallUtil.getInstance().setConnectingHfpMac("");
                    }
                    if(isA2dpAction||isA2dpSinkAction){
                        CallUtil.getInstance().setConnectingA2dpMac("");
                    }
                    // 蓝牙设备已连接
                    //BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    deviceSet.remove(device);
                    deviceSet.add(device);
                    if (deviceViewModel != null) {
                        deviceViewModel.setDeviceSet(deviceSet);
                    }
                    Log.d(TAG, "连接成功");
                    bluetoothManager.connect();
                    bluetoothManager.openBluetoothModule(null);
                    // 处理已连接的设备
                    Global.connStatus = Global.CONNECTED;

                }
            }

        }
        /*if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            // 蓝牙设备已连接
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            deviceSet.remove(device);
            deviceSet.add(device);
            if (deviceViewModel != null) {
                deviceViewModel.setDeviceSet(deviceSet);
            }
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
            if (deviceViewModel != null) {
                deviceViewModel.setDeviceSet(deviceSet);
            }
            Log.d(TAG, "断开连接");
            // 处理已断开连接的设备
//            bluetoothManager.stopContactOrHistoryLoad(null);
            Global.setContactList(new ArrayList<>());
            if (addressViewModel != null) {
                addressViewModel.setCallLogList(new ArrayList<>());
                addressViewModel.setContactList(new ArrayList<>());
            }
            if (musicViewModel != null) {
                musicViewModel.setMusicName("");
                musicViewModel.setArtist("");
                musicViewModel.setA2dpStatus(IVIBluetooth.BluetoothA2DPStatus.READY);
            }
            Global.connStatus = Global.NOT_CONNECTED;
        }*/
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

    private BluetoothDevice getDeviceByMac(String mac){
        Set<BluetoothDevice> sets = BluetoothUtil.getBondedDevices();
        if(sets!=null && !sets.isEmpty()){
            for(BluetoothDevice device:sets){
                if(device.getAddress().equals(mac)){
                    return device;
                }
            }
        }
        return null;
    }

}
