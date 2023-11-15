package com.jancar.bluetooth.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jancar.bluetooth.R;
import com.jancar.sdk.bluetooth.IVIBluetooth;

public class CallWindowUtil {

    private Context mContext = null;
    private WindowManager mWindowManager = null;
    private Handler mHandler = null;

    public CallWindowUtil(Context mContext){
        this.mContext = mContext;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mHandler = new Handler();
    }

    private boolean isShowCallWindow = false;
    private View callWindowView = null;
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

    public void showCallWindow(){

        if(isShowCallWindow){
            return;
        }
        isShowCallWindow = true;
        LayoutInflater  layoutInflater = LayoutInflater.from(mContext);
        callWindowView = layoutInflater.inflate(R.layout.activity_new_call_acitivity,null);
        keyboardLayout = callWindowView.findViewById(R.id.keyboardLayout);
        keyboardText = callWindowView.findViewById(R.id.keyboardText);
        number1Btn = callWindowView.findViewById(R.id.number1Btn);
        number2Btn = callWindowView.findViewById(R.id.number2Btn);
        number3Btn = callWindowView.findViewById(R.id.number3Btn);
        number4Btn = callWindowView.findViewById(R.id.number4Btn);
        number5Btn = callWindowView.findViewById(R.id.number5Btn);
        number6Btn = callWindowView.findViewById(R.id.number6Btn);
        number7Btn = callWindowView.findViewById(R.id.number7Btn);
        number8Btn = callWindowView.findViewById(R.id.number8Btn);
        number9Btn = callWindowView.findViewById(R.id.number9Btn);
        numberXinBtn = callWindowView.findViewById(R.id.numberXinBtn);
        number0Btn = callWindowView.findViewById(R.id.number0Btn);
        numberJinBtn = callWindowView.findViewById(R.id.numberJinBtn);

        callNameText = callWindowView.findViewById(R.id.callNameText);
        statusText = callWindowView.findViewById(R.id.statusText);
        timeText = callWindowView.findViewById(R.id.timeText);
        acceptBtn = callWindowView.findViewById(R.id.acceptBtn);
        hangupBtn = callWindowView.findViewById(R.id.hangupBtn);
        switchBtn = callWindowView.findViewById(R.id.switchBtn);

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

        WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        mWindowManager.addView(callWindowView, mLayoutParams);

        changeViewByStatus(CallUtil.getInstance().getCallStatus());
        changeVoiceStatus();

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

    public void changeVoiceStatus(){
        if(CallUtil.getInstance().isVoiceInCar()){
            switchBtn.setText("切到手机");
        }else{
            switchBtn.setText("切到车机");
        }
    }

    public void changeViewByStatus(int status){

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
                hideCallWindow();
                break;
        }

    }

    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if(isShowCallWindow && callWindowView!=null){
                int time = CallUtil.getInstance().getCallTime();
                timeText.setText(CallUtil.getInstance().getCallingTime(time));
                mHandler.postDelayed(updateTimeRunnable,1000);

            }
        }
    };

    public boolean isShowCallWindow(){
        return isShowCallWindow;
    }

    public void hideCallWindow(){

        if(callWindowView!=null){
            mWindowManager.removeViewImmediate(callWindowView);
            callWindowView = null;
        }
        isShowCallWindow = false;

    }



}
