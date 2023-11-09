package com.jancar.bluetooth.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelStore;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.viewmodels.CallViewModel;
import com.jancar.sdk.bluetooth.BluetoothManager;
import com.jancar.sdk.bluetooth.IVIBluetooth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class CallActivity extends AppCompatActivity {

    private final static String TAG = "CallActivity";
    private TextView callNumberTv, callNameTv, callStatusTv;
    private Button answerBtn, hangUpBtn;
    private CallViewModel callViewModel;
    private BluetoothManager bluetoothManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        initView(this);
        init();
        Intent intent = getIntent();
        callViewModel.getIsComing().observe(this, aBoolean -> {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) hangUpBtn.getLayoutParams();
            if(aBoolean) {
               answerBtn.setVisibility(View.VISIBLE);
               layoutParams.horizontalBias = 0.6f;
               hangUpBtn.setLayoutParams(layoutParams);
               callStatusTv.setText(getText(R.string.str_incoming));
            } else {
                answerBtn.setVisibility(View.INVISIBLE);
                layoutParams.horizontalBias = 0.5f;
                hangUpBtn.setLayoutParams(layoutParams);
                callStatusTv.setText(getText(R.string.str_dialing));
            }
        });
        callViewModel.getName().observe(this, name -> {
            callNameTv.setText(name);
        });
        callViewModel.getNumber().observe(this, number -> {
            callNumberTv.setText(number);
        });
        callViewModel.setIsComing(intent.getBooleanExtra(Global.EXTRA_IS_COMING, false));
        callViewModel.setName(intent.getStringExtra(Global.EXTRA_NAME));
        callViewModel.setNumber(intent.getStringExtra(Global.EXTRA_NUMBER));
    }

    private void init() {
        EventBus.getDefault().register(this);
        callViewModel = new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory()).get(CallViewModel.class);
        bluetoothManager = MainApplication.getInstance().getBluetoothManager();
        hangUpBtn.setOnClickListener(v -> {
            bluetoothManager.hangPhone(null);
            finish();
        });
        answerBtn.setOnClickListener(v -> {
            bluetoothManager.listenPhone(null);
            switchToDial();
            finish();
        });
    }
    private void initView(AppCompatActivity activity) {
        callNumberTv = activity.findViewById(R.id.tv_call_number);
        callNameTv = activity.findViewById(R.id.tv_call_name);
        callStatusTv = activity.findViewById(R.id.tv_call_status);
        answerBtn = activity.findViewById(R.id.btn_call_answer);
        hangUpBtn = activity.findViewById(R.id.btn_call_hang_up);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventPhoneStatus(IVIBluetooth.CallStatus event) {
        Log.i(TAG, event.toString());
        switch (event.mStatus) {
            case IVIBluetooth.CallStatus.HANGUP:
                finish();
                break;
            case IVIBluetooth.CallStatus.TALKING:
                switchToDial();
                finish();
                break;
        }
    }

    private void switchToDial() {
        String number = callViewModel.getNumber().getValue();
        String name = callViewModel.getName().getValue();
        Intent intent = new Intent(CallActivity.this, DialActivity.class);
        intent.putExtra(Global.EXTRA_NUMBER, number);
        intent.putExtra(Global.EXTRA_NAME, name);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}