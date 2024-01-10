package com.jancar.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SortDeviceListTask extends AsyncTask<Set<BluetoothDevice>, Void, List<BluetoothDevice>> {
    private final OnTaskCompleteListener listener;

    public SortDeviceListTask(OnTaskCompleteListener listener) {
        this.listener = listener;
    }

    @Override
    protected List<BluetoothDevice> doInBackground(Set<BluetoothDevice>... sets) {
        Set<BluetoothDevice> deviceSet = sets[0];
        List<BluetoothDevice> sortDeviceList = new ArrayList<>();
        List<BluetoothDevice> tempList;

        if (deviceSet != null) {
            tempList = new ArrayList<>(deviceSet);
        } else {
            tempList = new ArrayList<>();
        }

        if (deviceSet != null) {
            for (BluetoothDevice device : deviceSet) {
                if (device.isConnected()) {
                    sortDeviceList.add(device);
                    tempList.remove(device);
                }
            }
        }

        deviceSet = new HashSet<>(tempList);

        for (BluetoothDevice device : deviceSet) {
            if (device.getBondState() == BluetoothDevice.BOND_BONDED
                    || device.getBondState() == BluetoothDevice.BOND_BONDING) {
                sortDeviceList.add(device);
                tempList.remove(device);
            }
        }

        sortDeviceList.addAll(tempList);
        return sortDeviceList;
    }

    @Override
    protected void onPostExecute(List<BluetoothDevice> sortedList) {
        super.onPostExecute(sortedList);
        if (listener != null) {
            listener.onTaskComplete(sortedList);
        }
    }

    public interface OnTaskCompleteListener {
        void onTaskComplete(List<BluetoothDevice> sortedList);
    }
}
