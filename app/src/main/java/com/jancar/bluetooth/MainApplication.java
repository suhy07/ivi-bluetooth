package com.jancar.bluetooth;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.jancar.bluetooth.service.BluetoothService;
import com.jancar.bluetooth.utils.BluetoothUtil;
import com.jancar.bluetooth.utils.CallUtil;
import com.jancar.bluetooth.utils.CallWindowUtil;
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

    private CallWindowUtil mCallWindowUtil = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        BluetoothUtil.setContext(this);
        getBluetoothManager();
        bluetoothManager.connect();
        EventBus.getDefault().register(this);

        startService();
        CallUtil.getInstance();
        mCallWindowUtil = new CallWindowUtil(mInstance);
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
        if(event.mStatus == IVIBluetooth.CallStatus.INCOMING||
                event.mStatus == IVIBluetooth.CallStatus.OUTGOING||event.mStatus == IVIBluetooth.CallStatus.TALKING) {

            CallUtil.getInstance().setCallNumber(event.mPhoneNumber);
            CallUtil.getInstance().setCallName(event.mContactName);
            CallUtil.getInstance().setCallStatus(event.mStatus);

            if(!mCallWindowUtil.isShowCallWindow()){
                mCallWindowUtil.showCallWindow();
            }else{
                mCallWindowUtil.changeViewByStatus(event.mStatus);
            }


        }else if(event.mStatus == IVIBluetooth.CallStatus.HANGUP){
            mCallWindowUtil.hideCallWindow();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventVoiceChange(IVIBluetooth.EventVoiceChange event) {
        if (event != null) {
            CallUtil.getInstance().setVoiceInCar(event.type == IVIBluetooth.BluetoothAudioTransferStatus.HF_STATUS);
            if(mCallWindowUtil.isShowCallWindow()){
                mCallWindowUtil.changeVoiceStatus();
            }
        }
    }
}
