package com.jancar.bluetooth;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.service.BluetoothService;
import com.jancar.bluetooth.ui.CallActivity;
import com.jancar.bluetooth.ui.MainActivity;
import com.jancar.bluetooth.utils.BluetoothUtil;
import com.jancar.sdk.BaseManager;
import com.jancar.sdk.bluetooth.BluetoothManager;
import com.jancar.sdk.bluetooth.IVIBluetooth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


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
            bluetoothManager.openBluetoothModule(null);
            bluetoothManager.setAutoLink(true, null);
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
        getBluetoothManager();
        bluetoothManager.connect();
        EventBus.getDefault().register(this);

        startService();
    }

    private void startService(){

        Intent intent = new Intent();
        intent.setClass(mInstance, BluetoothService.class);
        startService(intent);

    }

    public static void showToast(String val) {
        Toast.makeText(getInstance(), val, Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventPhoneStatus(IVIBluetooth.CallStatus event) {
        Log.i("MainApplication", event.toString());
        if(event.mStatus == IVIBluetooth.CallStatus.INCOMING ||
                event.mStatus == IVIBluetooth.CallStatus.OUTGOING) {
            boolean isComing = (event.mStatus == IVIBluetooth.CallStatus.INCOMING);
            String number = event.mPhoneNumber;
            String name = Global.findNameByNumber(number);
            Intent intent = new Intent(mInstance, CallActivity.class);
            intent.putExtra(Global.EXTRA_IS_COMING, isComing);
            intent.putExtra(Intent.EXTRA_PHONE_NUMBER, number);
            intent.putExtra(Global.EXTRA_NAME, name);
            startActivity(intent);


        }
    }
}
