package com.jancar.bluetooth;

import android.app.Application;
import android.widget.Toast;

import com.jancar.bluetooth.utils.BluetoothUtil;

import dagger.hilt.android.HiltAndroidApp;

/**
 * @author suhy
 */
@HiltAndroidApp
public class MainApplication extends Application {
    private static MainApplication mInstance = null;
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
