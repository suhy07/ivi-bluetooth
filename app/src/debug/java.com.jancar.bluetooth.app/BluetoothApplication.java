package com.jancar.bluetooth.app;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.jancar.bluetooth.BuildConfig;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.service.BluetoothService;
import com.jancar.bluetooth.ui.CallActivity;
import com.jancar.bluetooth.ui.MainActivity;
import com.jancar.bluetooth.utils.BluetoothUtil;
import com.jancar.bluetooth.utils.CallUtil;
import com.jancar.bluetooth.utils.CallWindowUtil;
import com.jancar.sdk.BaseManager;
import com.jancar.sdk.bluetooth.BluetoothManager;
import com.jancar.sdk.bluetooth.IVIBluetooth;
import com.jancar.sdk.system.IVISystem;
import com.jancar.sdk.system.SystemManager;
import com.jancar.services.system.ISystemCallback;
import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.goldze.mvvmhabit.base.BaseApplication;
import me.goldze.mvvmhabit.crash.CaocConfig;
import me.goldze.mvvmhabit.utils.KLog;


/**
 * @author suhy
 */
public class BluetoothApplication extends BaseApplication {
    private static BluetoothApplication mInstance = null;
    private BluetoothManager bluetoothManager = null;
    public SystemManager mSystemManager = null;
    private Handler mHandler;
    private static Toast mToast;
    public ExecutorService executor;
    public static BluetoothApplication getInstance() {
        if (mInstance == null){
            synchronized (BluetoothApplication.class) {
                if (mInstance == null) {
                    mInstance = new BluetoothApplication();
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
        mHandler = new Handler();
        mInstance = this;
        BluetoothUtil.setContext(this);
        getBluetoothManager();
        bluetoothManager.connect();
        executor = new ThreadPoolExecutor(2, // 核心线程数
                5, // 最大线程数
                10, // 线程空闲时间
                TimeUnit.SECONDS, // 时间单位
                new ArrayBlockingQueue<>(10));  // 任务队列
        EventBus.getDefault().register(this);

        startService();
        CallUtil.getInstance();
        mCallWindowUtil = new CallWindowUtil(mInstance);
        registerBroadcastReceiver();
        mSystemManager = new SystemManager(mInstance, ConnectListen_System);
        //是否开启打印日志
        KLog.init(BuildConfig.DEBUG);
        //初始化全局异常崩溃
        initCrash();
        //内存泄漏检测
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }
    }

    private void initCrash() {
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
                .enabled(true) //是否启动全局异常捕获
                .showErrorDetails(true) //是否显示错误详细信息
                .showRestartButton(true) //是否显示重启按钮
                .trackActivities(true) //是否跟踪Activity
                .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
                .errorDrawable(R.mipmap.ic_launcher) //错误图标
                .restartActivity(MainActivity.class) //重新启动后的activity
//                .errorActivity(YourCustomErrorActivity.class) //崩溃后的错误activity
//                .eventListener(new YourCustomEventListener()) //崩溃后的错误监听
                .apply();
    }

    BaseManager.ConnectListener ConnectListen_System = new BaseManager.ConnectListener() {
        @Override
        public void onServiceConnected() {
            Log.i("MainApplication", "SystemManager onServiceConnected");
            mSystemManager.registerSystemCallback(mSystemCallback);
        }

        @Override
        public void onServiceDisconnected() {
            Log.i("MainApplication", "SystemManager onServiceDisconnected");
            mSystemManager.unRegisterSystemCallback(mSystemCallback);
        }
    };

    ISystemCallback.Stub mSystemCallback = new ISystemCallback.Stub() {
        @Override
        public void onOpenScreen(int from) throws RemoteException {

        }

        @Override
        public void onCloseScreen(int from) throws RemoteException {

        }

        @Override
        public void onScreenBrightnessChange(int id, int brightness) throws RemoteException {

        }

        @Override
        public void onCurrentScreenBrightnessChange(int id, int brightness) throws RemoteException {

        }

        @Override
        public void quitApp() throws RemoteException {
            //finishActivity();
            Log.i("MainApplication", "quitApp called");
            mInstance.sendBroadcast(new Intent(MainActivity.ACTION_QUITE_APP));
        }

        @Override
        public void startNavigationApp() throws RemoteException {

        }

        @Override
        public void onMediaAppChanged(String packageName, boolean isOpen) throws RemoteException {

        }

        @Override
        public void gotoSleep() throws RemoteException {

        }

        @Override
        public void wakeUp() throws RemoteException {

        }

        @Override
        public void onFloatBarVisibility(int visibility) throws RemoteException {

        }

        @Override
        public void onTboxChange(boolean isOpen) throws RemoteException {

        }

        @Override
        public void onScreenProtection(boolean isEnterScreenProtection) throws RemoteException {

        }

        @Override
        public void onTelPhoneStatusChange(int status, String phoneNumber, String phoneName) throws RemoteException {

        }

        @Override
        public void onTouchEventPos(int x, int y) throws RemoteException {

        }
    };

    private void startService(){

        Intent intent = new Intent();
        intent.setClass(mInstance, BluetoothService.class);
        startService(intent);

    }

    private static long lastShowTime = 0;

    public static void showToast(String val) {
        long tempTime = System.currentTimeMillis();
        long diff = tempTime - lastShowTime;
        if(diff<2500){
            return;
        }
        lastShowTime = tempTime;

        if (mToast == null) {
            mToast = Toast.makeText(getInstance(), val, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(val);
        }
        mToast.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventPhoneStatus(IVIBluetooth.CallStatus event) {
        Log.i("MainApplication", event.toString());
        if(event.mStatus == IVIBluetooth.CallStatus.INCOMING||
                event.mStatus == IVIBluetooth.CallStatus.OUTGOING||event.mStatus == IVIBluetooth.CallStatus.TALKING) {


            CallUtil.getInstance().setCallNumber(event.mPhoneNumber);
            //CallUtil.getInstance().setCallName(event.mContactName);
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
            mHandler.removeCallbacks(hideCallWindowRunnable);
            if(IVISystem.ACTION_BACKCAR_FINISH.equals(action)){
                isBackCar = false;
                mCallWindowUtil.setBackCar(false);
                if(isAutoConnected() && isInAutoScreen()){
                    mCallWindowUtil.hideSmallCallWindow();
                }else{
                    if(mCallWindowUtil.isShowSmallCallWindow()){
                        mCallWindowUtil.hideSmallCallWindow();
                        mCallWindowUtil.showCallWindow();
                    }
                }
            }else if(IVISystem.ACTION_BACKCAR_STARTED.equals(action)){
                isBackCar = true;
                mCallWindowUtil.setBackCar(true);
                /*if(mCallWindowUtil.isShowCallWindow()){
                    mCallWindowUtil.hideCallWindow();
                    mCallWindowUtil.showSmallCallWindow();
                }*/
                mHandler.postDelayed(hideCallWindowRunnable,1000);
            }
        }
    };

    private Runnable hideCallWindowRunnable = new Runnable() {
        @Override
        public void run() {
            if(isBackCar){
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
        if(appTask!=null && appTask.size()>0){
            String topActivityName = ((ActivityManager.RunningTaskInfo)appTask.get(0)).topActivity.getClassName();
            Log.i("MainApplication", "topActivityName:"+topActivityName);
            return topActivityName.equals("com.google.android.projection.sink.ui.AndroidAutoActivity")||topActivityName.equals(IVISystem.ACTIVITY_ANDROID_AUTO);
        }
        return false;
    }





}
