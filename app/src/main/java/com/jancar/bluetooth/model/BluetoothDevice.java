package com.jancar.bluetooth.model;

import java.util.Objects;

/**
 * @author suhy
 */
public class BluetoothDevice {
    private String name;
    private String address;
    private int pairStatus = android.bluetooth.BluetoothDevice.BOND_NONE;
    private boolean connectStatus = false;

    public BluetoothDevice() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BluetoothDevice)){
            return false;
        }
        BluetoothDevice that = (BluetoothDevice) o;
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    public int getPairStatus() {
        return pairStatus;
    }

    public void setPairStatus(int pairStatus) {
        this.pairStatus = pairStatus;
    }

    public boolean getConnectStatus() {
        return connectStatus;
    }

    public void setConnectStatus(boolean connectStatus) {
        this.connectStatus = connectStatus;
    }

    public BluetoothDevice(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
