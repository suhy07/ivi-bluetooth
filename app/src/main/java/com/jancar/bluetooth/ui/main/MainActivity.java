package com.jancar.bluetooth.ui.main;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.jancar.bluetooth.BR;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.databinding.ActivityMainBinding;
import com.jancar.bluetooth.service.BluetoothService;
import com.jancar.bluetooth.ui.main.vp.MainGroupFragment;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * @author suhy
 */
public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    @Override
    public void initData() {
        startContainerActivity(MainGroupFragment.class.getCanonicalName());
    }

    @Override
    public void initViewObservable() {
    }
    private final static String TAG = "MainActivity";
    private static boolean isFirst = true;
    private BluetoothService bluetoothService;
    private ServiceConnection serviceConnection;

    public static final String ACTION_QUITE_APP = "com.jancar.bluetooth.action.quit_app_now";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        setLayoutStable();
        setContentView(R.layout.activity_main);
        init();
        onNewIntent(getIntent());
        if(isFirst) {
            isFirst = false;
//            viewPager.setCurrentItem(3);
        }
        registerBroadcastReceiver();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int nowPage = 3;
        int pageNum = intent.getIntExtra("page_num", nowPage);
        Log.i(TAG, "onNewIntent： PageNum:" + pageNum);
//        viewPager.setCurrentItem(pageNum);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //   super.onSaveInstanceState(outState);
    }


    private void init(){
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) iBinder;
                bluetoothService = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                bluetoothService = null;
            }
        };
        Intent serviceIntent = new Intent(MainActivity.this, BluetoothService.class);
        // 启动服务
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isFirst = true;
        unbindService(serviceConnection);
        unregisterReceiver(mBroadcastReceiver);
    }

    private void registerBroadcastReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_QUITE_APP);
        registerReceiver(mBroadcastReceiver, filter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("MainActivity", "onReceive action:"+action);
            if(ACTION_QUITE_APP.equals(action)){
                if(MainActivity.this.isFinishing() || MainActivity.this.isDestroyed()){

                }else{
                    Log.i("MainActivity", "onReceive action:"+action+" try finish");
                    MainActivity.this.finishAndRemoveTask();
                }
            }
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    private void setLayoutStable() {
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
    }
}