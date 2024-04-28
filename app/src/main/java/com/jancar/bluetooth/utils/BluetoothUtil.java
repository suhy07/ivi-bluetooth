package com.jancar.bluetooth.utils;



import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.SparseArray;


import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.model.Contact;
import com.jancar.btservice.bluetooth.BluetoothVCardBook;
import com.jancar.btservice.bluetooth.IBluetoothExecCallback;
import com.jancar.sdk.bluetooth.IVIBluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * @author suhy
 */
public class BluetoothUtil {

    private static BluetoothSocket bluetoothSocket;
    private final static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private boolean mIsNewConnectedDevice = false;
    private static Context context;

    private static BluetoothUtil mInstance = null;

    private BluetoothUtil() {

    }

    public static BluetoothUtil getInstance() {
        if (mInstance == null) {
            synchronized (BluetoothUtil.class) {
                if (mInstance == null) {
                    mInstance = new BluetoothUtil();
                }
            }
        }
        return mInstance;
    }

    public static void connectToDevice(String deviceAddress) {
        Global global = Global.getInstance();
        BluetoothDevice targetDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
        try {
            bluetoothSocket = targetDevice.createRfcommSocketToServiceRecord(Global.getUUID());
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

    public static Set<BluetoothDevice> getBondedDevices() {
        return bluetoothAdapter.getBondedDevices();
    }

    public static String getPairingStatus(BluetoothDevice device) {
        int bondState = device.getBondState();
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

    public static String getConnectStatus(BluetoothDevice device) {
        String connectStatus = context.getString(R.string.conn_status_not);
        if(CallUtil.getInstance().isDeviceConnected(device)){
            connectStatus = context.getString(R.string.conn_status_connected);
        }else if(CallUtil.getInstance().isDeviceConnecting(device)){
            connectStatus = context.getString(R.string.conn_status_connecting);
        }
        return connectStatus;
    }

    public static void setContext(Context context) {
        BluetoothUtil.context = context;
    }

}