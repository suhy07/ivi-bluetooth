package com.jancar.bluetooth.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jancar.bluetooth.R;
import com.jancar.bluetooth.utils.CallUtil;
import com.jancar.sdk.bluetooth.IVIBluetooth;
import com.jancar.sdk.system.IVISystem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class CallActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        initView();
    }

    private void initView(){

        registerBroadcastReceiver();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        unregisterReceiver(mBroadcastReceiver);
    }

    private void registerBroadcastReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(IVISystem.ACTION_BACKCAR_FINISH);
        filter.addAction(IVISystem.ACTION_BACKCAR_STARTED);
        registerReceiver(mBroadcastReceiver, filter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(IVISystem.ACTION_BACKCAR_FINISH.equals(action)){

            }else if(IVISystem.ACTION_BACKCAR_STARTED.equals(action)){
                CallActivity.this.finish();
            }
        }
    };

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventPhoneStatus(IVIBluetooth.CallStatus event) {
        Log.i("MainApplication", event.toString());
        if (event.mStatus == IVIBluetooth.CallStatus.HANGUP) {

            CallActivity.this.finish();

        }
    }
}