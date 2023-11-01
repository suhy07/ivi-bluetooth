package com.jancar.bluetooth.utils;



import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothPbapClient;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;


import com.jancar.bluetooth.R;
import com.jancar.bluetooth.global.Global;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * @author suhy
 */
public class BluetoothUtil {

    private static BluetoothSocket bluetoothSocket;
    private final static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static Context context;
    private static Context contextf;
    private static Context contexta;
    private static String TAG = "?!";

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

    public static Set<BluetoothDevice> getBondedDevices () {
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

    BluetoothPbapClient mPbapClient;
    boolean isPbapProfileReady;

    public void getProfileProxy() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isPbapService = bluetoothAdapter.getProfileProxy(contexta, new ProxyServiceListener(), BluetoothProfile.PBAP_CLIENT);
        Log.i(TAG, "getProfileProxy" + isPbapService);
    }

    private final class ProxyServiceListener implements BluetoothProfile.ServiceListener{

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            Log.d(TAG,"Bluetooth service connected profile == " + profile);
            if (profile == BluetoothProfile.PBAP_CLIENT) {
                mPbapClient = (BluetoothPbapClient) proxy;
                isPbapProfileReady = true;
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            Log.d(TAG, "BluetoothPbapClient Profile Proxy Disconnected");
            if (profile == BluetoothProfile.PBAP_CLIENT) {
                isPbapProfileReady = false;
//                mPbapClient = null;
            }
        }
    }

    // 连接
    public void connect(BluetoothDevice device) {
        if (null != mPbapClient) {
            Method m = null;
            try {
                Method connectMethod = mPbapClient.getClass().getMethod("connect", BluetoothDevice.class);
                boolean isConnected = (boolean) connectMethod.invoke(mPbapClient, device);
                if (isConnected) {
                    // 连接成功
                } else {
                    // 连接失败
                }
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }
        Log.i(TAG, "mPbapClient == null");
    }
    //断连
    public void disconnect(BluetoothDevice device) {
        if (mPbapClient != null) {
            try {
                Method disconnectMethod = mPbapClient.getClass().getMethod("disconnect", BluetoothDevice.class);
                boolean isDisconnected = (boolean) disconnectMethod.invoke(mPbapClient, device);
                if (isDisconnected) {
                    // 断连成功
                } else {
                    // 断连失败
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                // 处理反射调用异常
            }
        } else {
            // mPbapClient 为 null，无法断连
            Log.i(TAG, "mPbapClient == null");
        }
    }

    //判断连接状态
    public int getConnectionState() {
        if (null != mPbapClient) {
            List<BluetoothDevice> deviceList = mPbapClient.getConnectedDevices();
            if (deviceList.isEmpty()) {
                return BluetoothProfile.STATE_DISCONNECTED;
            } else {
                return mPbapClient.getConnectionState(deviceList.remove(0));
            }
        }
        return BluetoothProfile.STATE_DISCONNECTED;
    }


    public static void setContext(Context context) {
        BluetoothUtil.context = context;
    }

    public static void setContextf(Context contextf) {
        BluetoothUtil.contextf = contextf;
    }

    public static void setContexta(Context contexta) {
        BluetoothUtil.contexta = contexta;
    }
}
