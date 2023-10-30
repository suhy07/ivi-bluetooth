/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.jancar.bluetooth.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;


import com.jancar.bluetooth.R;
import com.jancar.bluetooth.global.Global;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author suhy
 */
public class BluetoothUtil {

    private static BluetoothSocket bluetoothSocket;


    private static Context context;

    private static BluetoothUtil mInstance = null;
    private BluetoothUtil() {

    }

    public static BluetoothUtil getInstance() {
        if (mInstance == null){
            synchronized (BluetoothUtil.class) {
                if (mInstance == null) {
                    mInstance = new BluetoothUtil();
                }
            }
        }
        return mInstance;
    }

    public static void connectToDevice(String deviceAddress, BluetoothAdapter bluetoothAdapter) {
        Global global = Global.getInstance();
        BluetoothDevice targetDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
        try {
            bluetoothSocket = targetDevice.createRfcommSocketToServiceRecord(global.MY_UUID);
            bluetoothSocket.connect();

        } catch (IOException e) {
            // 连接失败，处理异常
            e.printStackTrace();
            try {
                bluetoothSocket.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    public static Set<BluetoothDevice> getBondedDevices (BluetoothAdapter bluetoothAdapter) {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        return pairedDevices;
    }
    public static String getPairingStatus (int bondState) {
        String pairingStatus;
        switch (bondState) {
            case BluetoothDevice.BOND_BONDED:
                pairingStatus = context.getString(R.string.pair_status_paired);
                break;
            case BluetoothDevice.BOND_BONDING:
                pairingStatus = context.getString(R.string.pair_status_pairing);
                break;
            case BluetoothDevice.BOND_NONE:
                pairingStatus = context.getString(R.string.pair_status_unpaired);
                break;
            case -1:
                pairingStatus = context.getString(R.string.pair_status_failed);
                break;
            default:
                pairingStatus = context.getString(R.string.pair_status_unknown);
                break;
        }
        return pairingStatus;
    }

    public static String getConnectStatus (boolean status){
        String connectStatus;
        if(status){
            connectStatus = context.getString(R.string.conn_status_connected);
        } else {
            connectStatus = context.getString(R.string.conn_status_not);
        }
        return connectStatus;
    }

    public static void setContext(Context context) {
        BluetoothUtil.context = context;
    }
}
