package com.jancar.bluetooth.broadcast;

import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.util.Log;
import android.widget.Toast;

import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.utils.BluetoothUtil;
import com.jancar.bluetooth.utils.CallUtil;
import com.jancar.bluetooth.viewmodels.AddressViewModel;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;
import com.jancar.bluetooth.viewmodels.MusicViewModel;
import com.jancar.sdk.bluetooth.IVIBluetooth;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author suhy
 */
public class BluetoothStateReceiver extends BroadcastReceiver {
    private final static String TAG = "BluetoothStateReceiver";
    private DeviceViewModel deviceViewModel;
    private AddressViewModel addressViewModel;
    private MusicViewModel musicViewModel;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    // 蓝牙已关闭
                    if (deviceViewModel != null) {
                        deviceViewModel.setDeviceList(new ArrayList<>());
                        deviceViewModel.setOnOff(false);
                    }
                    Log.i(TAG, "蓝牙已关闭");
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
                    CallUtil.getInstance().setA2dpStatus(BluetoothProfile.STATE_DISCONNECTED);
                    CallUtil.getInstance().setHfpStatus(BluetoothProfile.STATE_DISCONNECTED);
                    break;

                case BluetoothAdapter.STATE_ON:
                    CallUtil.getInstance().init();
                    if (deviceViewModel != null) {
                        deviceViewModel.setDeviceList(new ArrayList<>(BluetoothUtil.getBondedDevices()));
                        deviceViewModel.setOnOff(true);
                    }
                    Log.i(TAG, "蓝牙已打开");
                    break;
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

}
