package com.jancar.bluetooth.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.viewmodels.DialViewModel;
import com.jancar.btservice.bluetooth.IBluetoothExecCallback;
import com.jancar.sdk.bluetooth.BluetoothManager;
import com.jancar.sdk.bluetooth.IVIBluetooth;
import com.jancar.sdk.utils.Logcat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DialActivity extends AppCompatActivity {
    private final static String TAG = "DialActivity";
    private BluetoothManager bluetoothManager;
    private DialViewModel dialViewModel;
    int btnCount = 10;
    private Button[] num = new Button[btnCount];
    private Button numaBtn, numbBtn, switchBtn, hangUpBtn;
    private EditText numberEt;
    private TextView dailNameTv, dailNumTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial);
        initView(this);
        init();
        dialViewModel.getCallNumber().observe(this, number -> {
            dailNumTv.setText(number);
        });
        dialViewModel.getCallName().observe(this, name -> {
            dailNameTv.setText(name);
        });
        dialViewModel.getEtNum().observe(this, etNum -> {
            numberEt.setText(etNum);
        });
        Intent intent = getIntent();
        dialViewModel.setCallName(intent.getStringExtra(Global.EXTRA_NAME));
        dialViewModel.setCallNumber(intent.getStringExtra(Global.EXTRA_NUMBER));
    }

    private void init() {
        EventBus.getDefault().register(this);
        dialViewModel = new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory()).get(DialViewModel.class);
        bluetoothManager = MainApplication.getInstance().getBluetoothManager();
        for (int i = 0; i < btnCount; i++) {
            int finalI = i;
            num[i].setOnClickListener(v -> {
                setCallNum(String.valueOf(finalI));
                switch (finalI) {
                    case 0:
                        requestDTMF('0');
                        break;
                    case 1:
                        requestDTMF('1');
                        break;
                    case 2:
                        requestDTMF('2');
                        break;
                    case 3:
                        requestDTMF('3');
                        break;
                    case 4:
                        requestDTMF('4');
                        break;
                    case 5:
                        requestDTMF('5');
                        break;
                    case 6:
                        requestDTMF('6');
                        break;
                    case 7:
                        requestDTMF('7');
                        break;
                    case 8:
                        requestDTMF('8');
                        break;
                    case 9:
                        requestDTMF('9');
                        break;
                }
            });
        }
        numaBtn.setOnClickListener(v -> {
            setCallNum("*");
            requestDTMF('*');
        });
        numbBtn.setOnClickListener(v -> {
            setCallNum("#");
            requestDTMF('#');
        });
        switchBtn.setOnClickListener(v -> {
            changeVoice();
        });
        hangUpBtn.setOnClickListener(v -> {
            bluetoothManager.hangPhone(null);
            finish();
        });
    }

    private void initView(AppCompatActivity activity) {
        num[0] = activity.findViewById(R.id.btn_dial_num0);
        num[1] = activity.findViewById(R.id.btn_dial_num1);
        num[2] = activity.findViewById(R.id.btn_dial_num2);
        num[3] = activity.findViewById(R.id.btn_dial_num3);
        num[4] = activity.findViewById(R.id.btn_dial_num4);
        num[5] = activity.findViewById(R.id.btn_dial_num5);
        num[6] = activity.findViewById(R.id.btn_dial_num6);
        num[7] = activity.findViewById(R.id.btn_dial_num7);
        num[8] = activity.findViewById(R.id.btn_dial_num8);
        num[9] = activity.findViewById(R.id.btn_dial_num9);
        numaBtn = activity.findViewById(R.id.btn_dial_a);
        numbBtn = activity.findViewById(R.id.btn_dial_b);
        switchBtn = activity.findViewById(R.id.btn_dial_switch);
        hangUpBtn = activity.findViewById(R.id.btn_dial_hang_up);
        numberEt = activity.findViewById(R.id.et_dial_number);
        dailNameTv = activity.findViewById(R.id.tv_dial_name);
        dailNumTv = activity.findViewById(R.id.tv_dial_number);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventPhoneStatus(IVIBluetooth.CallStatus event) {
        Log.i(TAG, event.toString());
        switch (event.mStatus) {
            case IVIBluetooth.CallStatus.HANGUP:
                finish();
                break;
        }
    }

    private void setCallNum(String s) {
        String callNum = dialViewModel.getEtNum().getValue();
        int len = callNum.length();
        int maxLen = 25;
        if (len >= maxLen) {
            return;
        }
        callNum += s;
        dialViewModel.setEtNum(callNum);
    }

    private void changeVoice() {
        BluetoothManager btManager = bluetoothManager;
        if (btManager == null) return;
        btManager.transferCall(new IBluetoothExecCallback.Stub() {
            @Override
            public void onSuccess(String msg) throws RemoteException {
                // 切换成功，UI不在这里刷新，在PhoneCallWindowManager onEventVoiceChange 方法刷新
                Logcat.d("msg:" + msg);
            }

            @Override
            public void onFailure(int errorCode) throws RemoteException {
                Logcat.d("errorCode:" + errorCode);
            }
        });
    }

    private void requestDTMF(final int code) {
        BluetoothManager btManager = bluetoothManager;
        if (btManager == null) return;
        btManager.requestDTMF(code, new IBluetoothExecCallback.Stub() {
            @Override
            public void onSuccess(String msg) throws RemoteException {

            }

            @Override
            public void onFailure(int errorCode) throws RemoteException {
                Logcat.d("errorCode:" + errorCode);
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}