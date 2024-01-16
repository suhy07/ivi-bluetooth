package com.jancar.bluetooth.ui.phone;

import android.annotation.NonNull;
import android.arch.lifecycle.Observer;
import android.content.res.Configuration;
import android.os.Bundle;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_phone, container, false);
        initView(rootView);
        init();
        if (phoneViewModel != null) {
            phoneViewModel.getCallNumber().observe(this, s -> callNum.setText(s));
            phoneViewModel.getConnectStatus().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
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


    }

    private long lastClickTime;
    private int lastClickId = -1;

    private boolean isClickBusy(int id){
        long tempTime = System.currentTimeMillis();
        long diff = tempTime - lastClickTime;
        if(lastClickId == id && diff<250){
            return true;
        }
        lastClickId = id;
        lastClickTime = tempTime;
        return false;
    }

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
                if(isClickBusy(v.getId())){
                    return;
                }
                setCallNum(finalI + "");
            });
        }
        num[0].setOnLongClickListener(v -> {
            setCallNum("+");
            return true;
        });
        numaBtn.setOnClickListener(v -> {
            if(isClickBusy(v.getId())){
                return;
            }
            setCallNum("*");
        });
        numbBtn.setOnClickListener(v -> {
            if(isClickBusy(v.getId())){
                return;
            }
            setCallNum("#");
        });
        cancelBtn.setOnClickListener(v -> {
            if(isClickBusy(v.getId())){
                return;
            }
            if (phoneViewModel != null) {
                String s = phoneViewModel.getCallNumber().getValue();
                int len = s.length();
                if(len > 0) {
                    phoneViewModel.setCallNumber(s.substring(0, len - 1));
                }
            }
        });
        cancelBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (phoneViewModel != null) {
                    phoneViewModel.setCallNumber("");
                }
                return true;
            }
        });
        callBtn.setOnClickListener(v -> {
            String number = "";
            if (phoneViewModel != null) {
                number = phoneViewModel.getCallNumber().getValue();
            }
            //号码为空或未连接蓝牙时，不能拨号
            if (!CallUtil.getInstance().canCallNumber()) {
                MainApplication.showToast(getString(R.string.str_not_connect_warn));
                return;
            } else if(number.equals("")) {
                if(CallUtil.getInstance().getCallNumber().equals("")){
                    MainApplication.showToast(getString(R.string.str_no_number));
                }else{
                    phoneViewModel.setCallNumber(CallUtil.getInstance().getCallNumber());
                }
                return;
            }
            bluetoothManager.callPhone(number, stub);
            EventBus.getDefault().post(new IVIBluetooth.CallStatus(IVIBluetooth.CallStatus.OUTGOING, number, false));
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventPhoneStatus(IVIBluetooth.CallStatus event) {
        if(event!=null && event.mStatus == IVIBluetooth.CallStatus.OUTGOING){
            String number = event.mPhoneNumber;
            if (phoneViewModel != null) {
                String tempValue = phoneViewModel.getCallNumber().getValue();
                if(tempValue!=null && tempValue.equals(number)){
                    phoneViewModel.setCallNumber("");
                }

            }
        }
    }


    private void setCallNum(String s) {
        if (phoneViewModel != null) {
            String callNum = phoneViewModel.getCallNumber().getValue();
            int len = callNum.length();
            int maxLen = 25;
            if (len >= maxLen) {
                return;
            }
            callNum += s;
            phoneViewModel.setCallNumber(callNum);
        }
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