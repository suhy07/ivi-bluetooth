package com.jancar.bluetooth.ui.phone;

import android.annotation.NonNull;
import android.arch.lifecycle.Observer;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;


import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.utils.CallUtil;
import com.jancar.bluetooth.viewmodels.PhoneViewModel;
import com.jancar.btservice.bluetooth.IBluetoothExecCallback;
import com.jancar.sdk.bluetooth.BluetoothManager;
import com.jancar.sdk.bluetooth.IVIBluetooth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author suhy
 */
public class PhoneFragment extends Fragment {
    private final static String TAG = "PhoneFragment";
    private final int btnCount = 10;
    private final Button[] num = new Button[btnCount];
    private Button numaBtn, numbBtn;
    private ImageButton callBtn, cancelBtn;
    private EditText callNum;
    private PhoneViewModel phoneViewModel;
    private BluetoothManager bluetoothManager;
    private StringBuilder dialNumber = new StringBuilder();

    private boolean canDial = true;

    private Handler mHandler = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_phone, container, false);
        initView(rootView);
        init();
        if (phoneViewModel != null) {
            //phoneViewModel.getCallNumber().observe(this, s -> callNum.setText(s));
            phoneViewModel.getConnectStatus().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    canDial = true;
                    if(aBoolean){
                        callNum.setHint(R.string.str_connected);
                    }else{
                        callNum.setHint(R.string.str_unconnected);
                    }
                }
            });
            phoneViewModel.setConnectStatus(CallUtil.getInstance().canCallNumber());
        }
        return rootView;
    }

    private void initView(View rootView) {
        num[0] = rootView.findViewById(R.id.btn_num0);
        num[1] = rootView.findViewById(R.id.btn_num1);
        num[2] = rootView.findViewById(R.id.btn_num2);
        num[3] = rootView.findViewById(R.id.btn_num3);
        num[4]= rootView.findViewById(R.id.btn_num4);
        num[5] = rootView.findViewById(R.id.btn_num5);
        num[6] = rootView.findViewById(R.id.btn_num6);
        num[7] = rootView.findViewById(R.id.btn_num7);
        num[8] = rootView.findViewById(R.id.btn_num8);
        num[9] = rootView.findViewById(R.id.btn_num9);
        numaBtn = rootView.findViewById(R.id.btn_a);
        numbBtn = rootView.findViewById(R.id.btn_b);
        callBtn = rootView.findViewById(R.id.btn_call);
        cancelBtn = rootView.findViewById(R.id.btn_cancel);
        cancelBtn = rootView.findViewById(R.id.btn_cancel);
        callNum = rootView.findViewById(R.id.tv_phone_number);

        mHandler = new Handler();
    }

    private long lastClickTime;
    private int lastClickId = -1;

    /*private boolean isClickBusy(int id){
        long tempTime = System.currentTimeMillis();
        long diff = tempTime - lastClickTime;
        if(lastClickId == id && diff<250){
            return true;
        }
        lastClickId = id;
        lastClickTime = tempTime;
        return false;
    }*/

    private void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        bluetoothManager = MainApplication.getInstance().getBluetoothManager();
//        phoneViewModel = new ViewModelProvider(this,
//                new ViewModelProvider.NewInstanceFactory()).get(PhoneViewModel.class);
        for(int i = 0; i < btnCount; i++) {
            int finalI = i;
            num[i].setOnClickListener(v -> {
                setCallNum(finalI + "");
            });
        }
        num[0].setOnLongClickListener(v -> {
            setCallNum("+");
            return true;
        });
        numaBtn.setOnClickListener(v -> {
            setCallNum("*");
        });
        numbBtn.setOnClickListener(v -> {
            setCallNum("#");
        });
        cancelBtn.setOnClickListener(v -> {

            //if (phoneViewModel != null) {
                //String s = phoneViewModel.getCallNumber().getValue();
                int len = dialNumber.length();
                if(len > 0) {
                    dialNumber.deleteCharAt(len-1);
                    //phoneViewModel.setCallNumber(s.substring(0, len - 1));
                    setNumberText();
                }
            //}
        });
        cancelBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dialNumber.setLength(0);
                /*if (phoneViewModel != null) {
                    phoneViewModel.setCallNumber("");
                }*/
                setNumberText();
                return true;
            }
        });
        callBtn.setOnClickListener(v -> {
            /*String number = "";
            if (phoneViewModel != null) {
                number = phoneViewModel.getCallNumber().getValue();
            }*/
            //号码为空或未连接蓝牙时，不能拨号
            if (!CallUtil.getInstance().canCallNumber()) {
                MainApplication.showToast(getString(R.string.str_not_connect_warn));
                return;
            } else if(dialNumber.length() == 0) {
                if(CallUtil.getInstance().getCallNumber().equals("")){
                    MainApplication.showToast(getString(R.string.str_no_number));
                }else{
                    dialNumber.setLength(0);
                    dialNumber.append(CallUtil.getInstance().getCallNumber());
                    //phoneViewModel.setCallNumber(CallUtil.getInstance().getCallNumber());
                    setNumberText();
                }
                return;
            }
            if(canDial){
                bluetoothManager.callPhone(dialNumber.toString(), stub);
                mHandler.removeCallbacks(setRunnable);
                canDial = false;
                mHandler.postDelayed(setRunnable,5000);
            }

            //EventBus.getDefault().post(new IVIBluetooth.CallStatus(IVIBluetooth.CallStatus.OUTGOING, dialNumber.toString(), false));
        });
    }

    private Runnable setRunnable = new Runnable() {
        @Override
        public void run() {
            canDial = true;
        }
    };

    private void setNumberText(){
        callNum.setText(dialNumber.toString());
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventPhoneStatus(IVIBluetooth.CallStatus event) {
        canDial = true;
        if(event!=null && event.mStatus == IVIBluetooth.CallStatus.OUTGOING){
            String number = event.mPhoneNumber;
            //if (phoneViewModel != null) {
                //String tempValue = phoneViewModel.getCallNumber().getValue();
                if(dialNumber.toString().equals(number)){
                    dialNumber.setLength(0);
                    setNumberText();
                    //phoneViewModel.setCallNumber("");
                }

            //}
        }
    }


    private void setCallNum(String s) {
        //if (phoneViewModel != null) {
           // String callNum = phoneViewModel.getCallNumber().getValue();
            int len = dialNumber.length();
            int maxLen = 25;
            if (len >= maxLen) {
                return;
            }
            dialNumber.append(s);
            setNumberText();
            //callNum += s;
            //phoneViewModel.setCallNumber(callNum);
        //}
    }

    private final IBluetoothExecCallback.Stub stub = new IBluetoothExecCallback.Stub() {
        @Override
        public void onSuccess(String s) {
            Log.i(TAG, s);
        }

        @Override
        public void onFailure(int i) {
            Log.i(TAG, i + "");
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void setPhoneViewModel(PhoneViewModel phoneViewModel) {
        this.phoneViewModel = phoneViewModel;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }
}