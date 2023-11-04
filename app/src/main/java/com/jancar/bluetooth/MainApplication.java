package com.jancar.bluetooth;

import android.app.Application;
import android.widget.Toast;

import com.jancar.bluetooth.utils.BluetoothUtil;
import com.jancar.sdk.BaseManager;
import com.jancar.sdk.bluetooth.BluetoothManager;


/**
 * @author suhy
 */
public class MainApplication extends Application {
    private static MainApplication mInstance = null;
    private BluetoothManager bluetoothManager = null;
    public static MainApplication getInstance() {
        if (mInstance == null){
            synchronized (MainApplication.class) {
                if (mInstance == null) {
                    mInstance = new MainApplication();
                }
            }
        }
        return mInstance;
    }

    public BluetoothManager getBluetoothManager() {
        if (bluetoothManager == null) {
            bluetoothManager = new BluetoothManager(this, connectListener);
        }
        return bluetoothManager;
    }

    private BaseManager.ConnectListener connectListener = new BaseManager.ConnectListener() {
        @Override
        public void onServiceConnected() {

        }

        @Override
        public void onServiceDisconnected() {

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        BluetoothUtil.setContext(this);
    }

    public static void showToast(String val) {
        Toast.makeText(getInstance(), val, Toast.LENGTH_SHORT).show();
    }
}
