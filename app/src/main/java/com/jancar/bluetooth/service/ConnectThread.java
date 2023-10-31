package com.jancar.bluetooth.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothPbapClient;
import android.bluetooth.BluetoothSocket;
import android.providers.settings.SystemSettingsProto;
import android.util.Log;

import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.utils.BluetoothUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author suhy
 */
public class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public ConnectThread(BluetoothDevice device) {
        BluetoothSocket tmp = null;
        mmDevice = device;

        try {
            tmp = device.createRfcommSocketToServiceRecord(Global.getUUID());
        } catch (IOException e) {
            Log.e("?!" , "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public void run() {
        bluetoothAdapter.cancelDiscovery();
        Log.d("?!", "bluetoothAdapter.cancelDiscovery();");
        try {
            mmSocket.connect();
        } catch (IOException connectException) {
            Log.d("?!", connectException.getMessage());
        }
    }
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("?!", "Could not close the client socket", e);
        }
    }
}

