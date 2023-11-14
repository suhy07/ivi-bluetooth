package com.jancar.bluetooth.ui;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jancar.bluetooth.R;
import com.jancar.bluetooth.utils.CallUtil;
import com.jancar.sdk.bluetooth.IVIBluetooth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class NewCallActivity extends AppCompatActivity {

    private static final String TAG = "NewCallActivity";

    public static boolean isShow = false;

    private LinearLayout keyboardLayout;
    private TextView keyboardText;
    private Button number1Btn;
    private Button number2Btn;
    private Button number3Btn;
    private Button number4Btn;
    private Button number5Btn;
    private Button number6Btn;
    private Button number7Btn;
    private Button number8Btn;
    private Button number9Btn;
    private Button numberXinBtn;
    private Button number0Btn;
    private Button numberJinBtn;

    private TextView callNameText;
    private TextView statusText;
    private TextView timeText;
    private Button acceptBtn;
    private Button hangupBtn;
    private Button switchBtn;

    private Activity mActivity = null;

    private Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_call_acitivity);
        initView();
        EventBus.getDefault().register(this);
    }

    private void initView(){

        mHandler = new Handler();

        mActivity = this;

        isShow = true;

        keyboardLayout = findViewById(R.id.keyboardLayout);
        keyboardText = findViewById(R.id.keyboardText);
        number1Btn = findViewById(R.id.number1Btn);
        number2Btn = findViewById(R.id.number2Btn);
        number3Btn = findViewById(R.id.number3Btn);
        number4Btn = findViewById(R.id.number4Btn);
        number5Btn = findViewById(R.id.number5Btn);
        number6Btn = findViewById(R.id.number6Btn);
        number7Btn = findViewById(R.id.number7Btn);
        number8Btn = findViewById(R.id.number8Btn);
        number9Btn = findViewById(R.id.number9Btn);
        numberXinBtn = findViewById(R.id.numberXinBtn);
        number0Btn = findViewById(R.id.number0Btn);
        numberJinBtn = findViewById(R.id.numberJinBtn);

        callNameText = findViewById(R.id.callNameText);
        statusText = findViewById(R.id.statusText);
        timeText = findViewById(R.id.timeText);
        acceptBtn = findViewById(R.id.acceptBtn);
        hangupBtn = findViewById(R.id.hangupBtn);
        switchBtn = findViewById(R.id.switchBtn);

        number1Btn.setOnClickListener(mOnClickListener);
        number2Btn.setOnClickListener(mOnClickListener);
        number3Btn.setOnClickListener(mOnClickListener);
        number4Btn.setOnClickListener(mOnClickListener);
        number5Btn.setOnClickListener(mOnClickListener);
        number6Btn.setOnClickListener(mOnClickListener);
        number7Btn.setOnClickListener(mOnClickListener);
        number8Btn.setOnClickListener(mOnClickListener);
        number9Btn.setOnClickListener(mOnClickListener);
        numberXinBtn.setOnClickListener(mOnClickListener);
        number0Btn.setOnClickListener(mOnClickListener);
        numberJinBtn.setOnClickListener(mOnClickListener);
        acceptBtn.setOnClickListener(mOnClickListener);
        hangupBtn.setOnClickListener(mOnClickListener);
        switchBtn.setOnClickListener(mOnClickListener);

        changeViewByStatus(CallUtil.getInstance().getCallStatus());
        changeVoiceStatus();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventPhoneStatus(IVIBluetooth.CallStatus event) {


        changeViewByStatus(event.mStatus);
    }

    private void changeViewByStatus(int status){

        callNameText.setText(CallUtil.getInstance().getCallNumber());

        switch(status){
            case IVIBluetooth.CallStatus.INCOMING:
                statusText.setText("来电中");
                timeText.setVisibility(View.GONE);
                switchBtn.setVisibility(View.GONE);
                acceptBtn.setVisibility(View.VISIBLE);
                hangupBtn.setVisibility(View.VISIBLE);
                keyboardLayout.setVisibility(View.GONE);
                break;
            case IVIBluetooth.CallStatus.OUTGOING:
                statusText.setText("拨号中");
                timeText.setVisibility(View.GONE);
                switchBtn.setVisibility(View.GONE);
                acceptBtn.setVisibility(View.GONE);
                hangupBtn.setVisibility(View.VISIBLE);
                keyboardLayout.setVisibility(View.GONE);
                break;
            case IVIBluetooth.CallStatus.TALKING:
                statusText.setText("通话中");
                timeText.setVisibility(View.VISIBLE);
                switchBtn.setVisibility(View.VISIBLE);
                acceptBtn.setVisibility(View.GONE);
                hangupBtn.setVisibility(View.VISIBLE);
                keyboardLayout.setVisibility(View.VISIBLE);
                mHandler.postDelayed(updateTimeRunnable,10);
                break;
            case IVIBluetooth.CallStatus.HANGUP:
                finish();
                break;
        }

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.switchBtn:
                    CallUtil.getInstance().switchVoice();
                    break;
                case R.id.acceptBtn:
                    CallUtil.getInstance().listenPhone();
                    break;
                case R.id.hangupBtn:
                    CallUtil.getInstance().hangupPhone();
                    break;
                case R.id.number1Btn:
                    click('1');
                    break;
                case R.id.number2Btn:
                    click('2');
                    break;
                case R.id.number3Btn:
                    click('3');
                    break;
                case R.id.number4Btn:
                    click('4');
                    break;
                case R.id.number5Btn:
                    click('5');
                    break;
                case R.id.number6Btn:
                    click('6');
                    break;
                case R.id.number7Btn:
                    click('7');
                    break;
                case R.id.number8Btn:
                    click('8');
                    break;
                case R.id.number9Btn:
                    click('9');
                    break;
                case R.id.numberXinBtn:
                    click('*');
                    break;
                case R.id.number0Btn:
                    click('0');
                    break;
                case R.id.numberJinBtn:
                    click('#');
                    break;
            }

        }
    };


    public void click(char param) {
        if (keyboardText != null) {
            String str = (String) keyboardText.getText();
            str += param;
            keyboardText.setText(str);
            CallUtil.getInstance().requestDTMF(param);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventVoiceChange(IVIBluetooth.EventVoiceChange event) {
        if (event != null) {
            CallUtil.getInstance().setVoiceInCar(event.type == IVIBluetooth.BluetoothAudioTransferStatus.HF_STATUS);
            changeVoiceStatus();
        }
    }

    private void changeVoiceStatus(){
        if(CallUtil.getInstance().isVoiceInCar()){
            switchBtn.setText("切到手机");
        }else{
            switchBtn.setText("切到车机");
        }
    }

    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if(mActivity!=null && !mActivity.isFinishing()){
                int time = CallUtil.getInstance().getCallTime();
                timeText.setText(CallUtil.getInstance().getCallingTime(time));
                mHandler.postDelayed(updateTimeRunnable,1000);

            }

        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        isShow = false;
        EventBus.getDefault().unregister(this);
    }
}