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
import com.jancar.bluetooth.viewmodels.PhoneViewModel;
import com.jancar.sdk.bluetooth.BluetoothManager;
import com.jancar.sdk.bluetooth.IVIBluetooth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private PhoneViewModel phoneViewModel;
    public static boolean needFresh = false;

    private List<BluetoothDevice> deviceList;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("liyongde","onReceive:"+action);
        needFresh = true;
        if (deviceViewModel != null && deviceViewModel.getDeviceList() != null
        && deviceViewModel.getDeviceList().getValue() != null) {
            deviceList = new ArrayList<>(deviceViewModel.getDeviceList().getValue());
        } else {
            deviceList = new ArrayList<>();
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

            if(phoneViewModel!=null){
                phoneViewModel.setConnectStatus(CallUtil.getInstance().canCallNumber());
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

                    deviceList.remove(device);

                    BluetoothDevice newDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device.getAddress());

                    if(newDevice!=null && !deviceList.contains(newDevice)){
                        deviceList.add(0, newDevice);
                    }

                    if (deviceViewModel != null) {
                        deviceViewModel.setDeviceList(deviceList);
                    }
                    Log.d(TAG, "断开连接");
                    // 处理已断开连接的设备
                    Global.setContactList(new ArrayList<>());
                    if (addressViewModel != null) {
                        Log.i(TAG, "清空联系人和电话");
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
                    deviceList.remove(device);
                    deviceList.add(0, device);
                    if (deviceViewModel != null) {
                        deviceViewModel.setDeviceList(deviceList);
                    }
                }else if(state == BluetoothProfile.STATE_CONNECTED){
                    if(isHfpAction){
                       CallUtil.getInstance().setConnectingHfpMac("");
                    }
                    if(isA2dpAction||isA2dpSinkAction){
                        CallUtil.getInstance().setConnectingA2dpMac("");
                    }
                    // 蓝牙设备已连接
                    deviceList.remove(device);
                    deviceList.add(0, device);
                    if (deviceViewModel != null) {
                        deviceViewModel.setDeviceList(deviceList);
                    }
                    Log.d(TAG, "连接成功");
                    bluetoothManager.connect();
                    bluetoothManager.openBluetoothModule(null);
                    // 处理已连接的设备
                    Global.connStatus = Global.CONNECTED;

                }
            }

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

    public void setPhoneViewModel(PhoneViewModel phoneViewModel) {
        this.phoneViewModel = phoneViewModel;
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
