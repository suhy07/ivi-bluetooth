package com.jancar.bluetooth.ui.device;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Color;
import android.support.annotation.NonNull;

import com.jancar.bluetooth.R;
import com.jancar.bluetooth.app.BluetoothApplication;
import com.jancar.bluetooth.utils.BluetoothUtil;
import com.jancar.bluetooth.utils.CallUtil;
import com.jancar.sdk.utils.Logcat;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.base.MultiItemViewModel;

/**
 * @author suhy
 */
public class DeviceItemViewModel extends MultiItemViewModel {

    public ObservableField<BluetoothDevice> device = new ObservableField<>();
    public ObservableField<String> deviceName = new ObservableField<>("");
    public ObservableField<String> devicePairStatus = new ObservableField<>("");
    public ObservableField<String> deviceAddress = new ObservableField<>("");
    public ObservableField<String> deviceConnectStatus = new ObservableField<>("");
    public ObservableInt color = new ObservableInt(Color.WHITE);

    public DeviceItemViewModel(@NonNull BaseViewModel viewModel, BluetoothDevice device) {
        super(viewModel);
        this.device.set(device);
        setDevice(device);
        this.device.addOnPropertyChangedCallback(deviceCallback);
    }

    private final Observable.OnPropertyChangedCallback deviceCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (sender == device) {
                ObservableField<?> observableField = (ObservableField<?>) sender;
                BluetoothDevice newDevice = (BluetoothDevice) observableField.get();
                device.set(newDevice);
                if (newDevice != null) {
                    setDevice(newDevice);
                }
            }
        }
    };

    private void setDevice(BluetoothDevice device) {
        String name = device.getName();
        if (name != null && !"".equals(name) && !"null".equals(name)) {
            this.deviceName.set(device.getName());
        } else {
            Logcat.i("deviceName: " + name);
            this.deviceName.set(BluetoothApplication.getInstance().getString(R.string.str_unknown_device));
        }
        this.deviceAddress.set(device.getAddress());
        this.devicePairStatus.set(BluetoothUtil.getPairingStatus(device));
        this.deviceConnectStatus.set(BluetoothUtil.getConnectStatus(device));
        int pairStatus = device.getBondState();
        if(CallUtil.getInstance().isDeviceConnecting(device) || CallUtil.getInstance()
                .isDeviceConnected(device) || pairStatus == BluetoothDevice.BOND_BONDING){
            this.color.set(0xFF00C2C2);
        }
    }


}
