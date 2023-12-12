package com.jancar.bluetooth;

import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.jancar.bluetooth.service.BluetoothService;
import com.jancar.bluetooth.ui.CallActivity;
import com.jancar.bluetooth.utils.BluetoothUtil;
import com.jancar.bluetooth.utils.CallUtil;
import com.jancar.bluetooth.utils.CallWindowUtil;
import com.jancar.sdk.BaseManager;
import com.jancar.sdk.bluetooth.BluetoothManager;
import com.jancar.sdk.bluetooth.IVIBluetooth;
import com.jancar.sdk.system.IVISystem;
import com.jancar.sdk.utils.ActivityUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


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
        registerBroadcastReceiver();
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

            if(mCallWindowUtil.isShowCallWindow()){
                if(isBackCar){
                    mCallWindowUtil.hideCallWindow();
                    mCallWindowUtil.showSmallCallWindow();
                }else{
                    mCallWindowUtil.changeViewByStatus(event.mStatus);
                }

            }else if(mCallWindowUtil.isShowSmallCallWindow()){
                mCallWindowUtil.changeSmallViewByStatus(event.mStatus);
            }else{
                if(isBackCar){
                    mCallWindowUtil.showSmallCallWindow();
                }else{
                    if(isAutoConnected() && isInAutoScreen()){

                    }else{
                        mCallWindowUtil.showCallWindow();
                    }

                }
            }



        }else if(event.mStatus == IVIBluetooth.CallStatus.HANGUP){
            mCallWindowUtil.hideCallWindow();
            mCallWindowUtil.hideSmallCallWindow();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventVoiceChange(IVIBluetooth.EventVoiceChange event) {
        if (event != null) {
            CallUtil.getInstance().setVoiceInCar(event.type == IVIBluetooth.BluetoothAudioTransferStatus.HF_STATUS);
            if(mCallWindowUtil.isShowCallWindow()||mCallWindowUtil.isShowSmallCallWindow()){
                mCallWindowUtil.changeVoiceStatus();
            }
        }
    }


    private void registerBroadcastReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(IVISystem.ACTION_BACKCAR_FINISH);
        filter.addAction(IVISystem.ACTION_BACKCAR_STARTED);
        registerReceiver(mBroadcastReceiver, filter);
    }

    private boolean isBackCar = false;


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("MainApplication", "onReceive action:"+action);
            if(IVISystem.ACTION_BACKCAR_FINISH.equals(action)){
                isBackCar = false;
                mCallWindowUtil.setBackCar(false);
                if(isAutoConnected() && isInAutoScreen()){
                    mCallWindowUtil.hideSmallCallWindow();
                }
            }else if(IVISystem.ACTION_BACKCAR_STARTED.equals(action)){
                isBackCar = true;
                mCallWindowUtil.setBackCar(true);
                if(mCallWindowUtil.isShowCallWindow()){
                    mCallWindowUtil.hideCallWindow();
                    mCallWindowUtil.showSmallCallWindow();
                }
            }
        }
    };


    public void startCallActivity(){
        Intent intent = new Intent();
        intent.setClass(this, CallActivity.class);
        startActivity(intent);
    }


    private boolean isAutoConnected(){
        String phoneMode = Settings.Global.getString(getContentResolver(),IVISystem.KEY_PHONE_MODE);
        Log.i("MainApplication", "isAutoConnected phoneMode:"+phoneMode);
        if(phoneMode == null || phoneMode.equals("")){
            return false;
        }
        return phoneMode.contains("auto");
    }

    private boolean isInAutoScreen(){
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> appTask = am.getRunningTasks(1);
        String topActivityName = ((ActivityManager.RunningTaskInfo)appTask.get(0)).topActivity.getClassName();
        Log.i("MainApplication", "topActivityName:"+topActivityName);
        return topActivityName.equals("com.google.android.projection.sink.ui.AndroidAutoActivity")||topActivityName.equals(IVISystem.ACTIVITY_ANDROID_AUTO);
    }





}
