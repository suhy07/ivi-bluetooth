package com.jancar.bluetooth.utils;

import android.app.Instrumentation;
import android.app.KeyguardManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.global.Global;
import com.jancar.sdk.bluetooth.IVIBluetooth;
import com.jancar.sdk.utils.Logcat;

public class CallWindowUtil {

    private Context mContext = null;
    private WindowManager mWindowManager = null;
    private Handler mHandler = null;

    private KeyguardManager mKeyguardManager ;
    private PowerManager mPowerManager;

    public boolean isBackCar() {
        return isBackCar;
    }

    public void setBackCar(boolean backCar) {
        isBackCar = backCar;
    }

    private boolean isBackCar = false;

    public CallWindowUtil(Context mContext){
        this.mContext = mContext;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mHandler = new Handler();
        mKeyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
        mPowerManager =  (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
    }

    private boolean isShowCallWindow = false;
    private View callWindowView = null;
    private LinearLayout keyboardLayout;
    private LinearLayout incomingLayout;
    private TextView number1Text;
    private TextView number2Text;
    private TextView number3Text;
    private TextView number4Text;
    private TextView number5Text;
    private TextView number6Text;
    private TextView number7Text;
    private TextView number8Text;
    private TextView number9Text;
    private TextView numberXinText;
    private TextView number0Text;
    private TextView numberJinText;

    private TextView callNameText;
    private TextView statusText;
    private TextView timeText;
    private TextView keyboardText;

    private TextView acceptText;
    private TextView smallHangupText;
    private TextView switchVoiceText;
    private TextView bigHangupText;

    public void showCallWindow(){

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        Log.i("liyongde","showCallWindow:"+displayMetrics.widthPixels+"_"+displayMetrics.heightPixels);

        if(isShowCallWindow){
            return;
        }

        MainApplication.getInstance().startCallActivity();

        isShowCallWindow = true;
        LayoutInflater  layoutInflater = LayoutInflater.from(mContext);
        callWindowView = layoutInflater.inflate(R.layout.big_window_call,null);
        keyboardLayout = callWindowView.findViewById(R.id.keyboardLayout);
        keyboardText = callWindowView.findViewById(R.id.keyboardText);
        incomingLayout = callWindowView.findViewById(R.id.incomingLayout);

        number1Text = callWindowView.findViewById(R.id.number1Text);
        number2Text = callWindowView.findViewById(R.id.number2Text);
        number3Text = callWindowView.findViewById(R.id.number3Text);
        number4Text = callWindowView.findViewById(R.id.number4Text);
        number5Text = callWindowView.findViewById(R.id.number5Text);
        number6Text = callWindowView.findViewById(R.id.number6Text);
        number7Text = callWindowView.findViewById(R.id.number7Text);
        number8Text = callWindowView.findViewById(R.id.number8Text);
        number9Text = callWindowView.findViewById(R.id.number9Text);
        numberXinText = callWindowView.findViewById(R.id.numberXinText);
        number0Text = callWindowView.findViewById(R.id.number0Text);
        numberJinText = callWindowView.findViewById(R.id.numberJinText);

        callNameText = callWindowView.findViewById(R.id.callNameText);
        statusText = callWindowView.findViewById(R.id.statusText);
        timeText = callWindowView.findViewById(R.id.timeText);
        acceptText = callWindowView.findViewById(R.id.acceptText);
        smallHangupText = callWindowView.findViewById(R.id.smallHangupText);
        switchVoiceText = callWindowView.findViewById(R.id.switchVoiceText);
        bigHangupText = callWindowView.findViewById(R.id.bigHangupText);

        number1Text.setOnClickListener(mOnClickListener);
        number2Text.setOnClickListener(mOnClickListener);
        number3Text.setOnClickListener(mOnClickListener);
        number4Text.setOnClickListener(mOnClickListener);
        number5Text.setOnClickListener(mOnClickListener);
        number6Text.setOnClickListener(mOnClickListener);
        number7Text.setOnClickListener(mOnClickListener);
        number8Text.setOnClickListener(mOnClickListener);
        number9Text.setOnClickListener(mOnClickListener);
        numberXinText.setOnClickListener(mOnClickListener);
        number0Text.setOnClickListener(mOnClickListener);
        numberJinText.setOnClickListener(mOnClickListener);
        acceptText.setOnClickListener(mOnClickListener);
        smallHangupText.setOnClickListener(mOnClickListener);
        switchVoiceText.setOnClickListener(mOnClickListener);
        bigHangupText.setOnClickListener(mOnClickListener);

        number0Text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                click('+');
                return true;
            }
        });

        WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags =  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        mWindowManager.addView(callWindowView, mLayoutParams);

        changeViewByStatus(CallUtil.getInstance().getCallStatus());
        changeVoiceStatus();


        sendKeycodeEvent(KeyEvent.KEYCODE_F1);

    }

    private static void sendKeycodeEvent(final int KEYCODE) {
        Logcat.d("KEYCODE " + KEYCODE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KEYCODE);
                } catch (SecurityException e) {
                    Logcat.d("catch SecurityException");
                } catch (ActivityNotFoundException e) {
                    Logcat.d("catch ActivityNotFoundException");
                }
            }
        }).start();
    }



    private void showNavi(View view){
        View decorView = view.getRootView();

        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

        decorView.setSystemUiVisibility(uiOptions);
    }

    private void hideNavi(View view){

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;

        view.setSystemUiVisibility(uiOptions);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.switchVoiceText:
                    CallUtil.getInstance().switchVoice();
                    break;
                case R.id.acceptText:
                    CallUtil.getInstance().listenPhone();
                    break;
                case R.id.smallHangupText:
                case R.id.bigHangupText:
                    CallUtil.getInstance().hangupPhone();
                    break;
                case R.id.number1Text:
                    click('1');
                    break;
                case R.id.number2Text:
                    click('2');
                    break;
                case R.id.number3Text:
                    click('3');
                    break;
                case R.id.number4Text:
                    click('4');
                    break;
                case R.id.number5Text:
                    click('5');
                    break;
                case R.id.number6Text:
                    click('6');
                    break;
                case R.id.number7Text:
                    click('7');
                    break;
                case R.id.number8Text:
                    click('8');
                    break;
                case R.id.number9Text:
                    click('9');
                    break;
                case R.id.numberXinText:
                    click('*');
                    break;
                case R.id.number0Text:
                    click('0');
                    break;
                case R.id.numberJinText:
                    click('#');
                    break;
            }

        }
    };

    public void click(char param) {
        if (keyboardText != null) {
            String str = (String) keyboardText.getText();
            str += param;
            setViewVisible(keyboardText,true);
            keyboardText.setText(str);
            setViewVisible(timeText,false);
            CallUtil.getInstance().requestDTMF(param);
        }
    }

    public void changeVoiceStatus(){
        if(CallUtil.getInstance().isVoiceInCar()){
            switchVoiceText.setText(R.string.audio_in_car);
        }else{
            switchVoiceText.setText(R.string.audio_in_phone);
        }
    }

    public void changeViewByStatus(int status){

        String name = Global.findNameByNumber(CallUtil.getInstance().getCallNumber());

        callNameText.setText(name);

        switch(status){
            case IVIBluetooth.CallStatus.INCOMING:
                statusText.setText(R.string.status_incoming);
                setViewVisible(statusText,true);
                setViewVisible(timeText,false);
                setViewVisible(keyboardText,false);
                setViewVisible(incomingLayout,true);
                setViewVisible(acceptText,true);
                setViewVisible(smallHangupText,true);
                setViewVisible(keyboardLayout,false);
                break;
            case IVIBluetooth.CallStatus.OUTGOING:
                statusText.setText(R.string.status_outgoing);
                setViewVisible(statusText,true);
                setViewVisible(timeText,false);
                setViewVisible(keyboardText,false);
                setViewVisible(incomingLayout,true);
                setViewVisible(acceptText,false);
                setViewVisible(smallHangupText,true);
                setViewVisible(keyboardLayout,false);
                break;
            case IVIBluetooth.CallStatus.TALKING:
                setViewVisible(statusText,false);
                String text = keyboardText.getText().toString();
                if(text.equals("")){
                    setViewVisible(timeText,true);
                    setViewVisible(keyboardText,false);
                }else{
                    setViewVisible(timeText,false);
                    setViewVisible(keyboardText,true);
                }
                setViewVisible(incomingLayout,false);
                setViewVisible(keyboardLayout,true);
                mHandler.postDelayed(updateTimeRunnable,10);
                break;
            case IVIBluetooth.CallStatus.HANGUP:
                hideCallWindow();
                break;
        }

    }

    private void setViewVisible(View view,boolean isVisible){
        if(isVisible){
            if(view.getVisibility() != View.VISIBLE){
                view.setVisibility(View.VISIBLE);
            }
        }else{
            if(view.getVisibility() != View.GONE){
                view.setVisibility(View.GONE);
            }
        }
    }

    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if(isShowCallWindow && callWindowView!=null){
                String text = keyboardText.getText().toString();
                if(text.equals("")){
                    int time = CallUtil.getInstance().getCallTime();
                    timeText.setText(CallUtil.getInstance().getCallingTime(time));
                    mHandler.postDelayed(updateTimeRunnable,1000);
                }

            }
        }
    };

    private Runnable updateSmallTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if(isShowSmallCallWindow && smallCallWindowView!=null){

                    int time = CallUtil.getInstance().getCallTime();
                    timeText.setText(CallUtil.getInstance().getCallingTime(time));
                    mHandler.postDelayed(updateSmallTimeRunnable,1000);

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

    public boolean isShowSmallCallWindow(){
        return isShowSmallCallWindow;
    }

    public void hideSmallCallWindow(){

        if(smallCallWindowView!=null){
            mWindowManager.removeViewImmediate(smallCallWindowView);
            smallCallWindowView = null;
        }
        isShowSmallCallWindow = false;
    }

    private boolean isShowSmallCallWindow = false;
    private View smallCallWindowView = null;

    public void showSmallCallWindow(){

        if(isShowSmallCallWindow){
            return;
        }
        isShowSmallCallWindow = true;

        LayoutInflater  layoutInflater = LayoutInflater.from(mContext);
        smallCallWindowView = layoutInflater.inflate(R.layout.small_window_call,null);


        callNameText = smallCallWindowView.findViewById(R.id.callNameText);
        statusText = smallCallWindowView.findViewById(R.id.statusText);
        timeText = smallCallWindowView.findViewById(R.id.timeText);

        acceptText = smallCallWindowView.findViewById(R.id.acceptText);
        smallHangupText = smallCallWindowView.findViewById(R.id.smallHangupText);
        switchVoiceText = smallCallWindowView.findViewById(R.id.switchVoiceText);

        smallCallWindowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isBackCar){
                    hideSmallCallWindow();
                    showCallWindow();
                }
            }
        });


        acceptText.setOnClickListener(mOnClickListener);
        smallHangupText.setOnClickListener(mOnClickListener);
        switchVoiceText.setOnClickListener(mOnClickListener);

        WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags =  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = 85;

        mWindowManager.addView(smallCallWindowView, mLayoutParams);

        changeSmallViewByStatus(CallUtil.getInstance().getCallStatus());
        changeVoiceStatus();

    }

    public void changeSmallViewByStatus(int status){

        String name = Global.findNameByNumber(CallUtil.getInstance().getCallNumber());

        callNameText.setText(name);

        switch(status){
            case IVIBluetooth.CallStatus.INCOMING:
                statusText.setText(R.string.status_incoming);
                setViewVisible(statusText,true);
                setViewVisible(timeText,false);
                setViewVisible(acceptText,true);
                setViewVisible(switchVoiceText,false);
                setViewVisible(smallHangupText,true);
                break;
            case IVIBluetooth.CallStatus.OUTGOING:
                statusText.setText(R.string.status_outgoing);
                setViewVisible(statusText,true);
                setViewVisible(timeText,false);
                setViewVisible(acceptText,false);
                setViewVisible(switchVoiceText,false);
                setViewVisible(smallHangupText,true);
                break;
            case IVIBluetooth.CallStatus.TALKING:
                setViewVisible(statusText,false);
                setViewVisible(timeText,true);
                setViewVisible(acceptText,false);
                setViewVisible(switchVoiceText,true);
                setViewVisible(smallHangupText,true);
                mHandler.postDelayed(updateSmallTimeRunnable,10);
                break;
            case IVIBluetooth.CallStatus.HANGUP:
                hideCallWindow();
                break;
        }

    }



}
