package com.jancar.bluetooth.model;


import java.util.Objects;
import android.bluetooth.BluetoothDevice;

/**
 * @author suhy
 */
public class MyBluetoothDevice {
    private BluetoothDevice bluetoothDevice;
    private int pairStatus = BluetoothDevice.BOND_NONE;
    private boolean connectStatus = false;

    public MyBluetoothDevice() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MyBluetoothDevice)){
            return false;
        }
        MyBluetoothDevice that = (MyBluetoothDevice) o;
        return Objects.equals(bluetoothDevice.getAddress(), that.bluetoothDevice.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(bluetoothDevice.getAddress());
    }

}
