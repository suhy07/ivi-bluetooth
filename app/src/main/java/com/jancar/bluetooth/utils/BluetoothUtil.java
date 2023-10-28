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


import com.jancar.bluetooth.global.Global;

import java.io.IOException;

/**
 * @author suhy
 */
public class BluetoothUtil {


    private static BluetoothSocket bluetoothSocket;

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
    public static String getPairingStatus (int bondState) {
        String pairingStatus;
        switch (bondState) {
            case BluetoothDevice.BOND_BONDED:
                pairingStatus = "已配对";
                break;
            case BluetoothDevice.BOND_BONDING:
                pairingStatus = "正在配对";
                break;
            case BluetoothDevice.BOND_NONE:
                pairingStatus = "未配对";
                break;
            default:
                pairingStatus = "未知状态";
                break;
        }
        return pairingStatus;
    }

    public static String getConnectStatus (boolean status){
        String connectStatus;
        if(status){
            connectStatus = "已连接";
        } else {
            connectStatus = "未连接";
        }
        return connectStatus;
    }
}
