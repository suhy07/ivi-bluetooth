package com.jancar.bluetooth.ui.phone;

import android.annotation.NonNull;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.os.RemoteException;
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
import com.jancar.bluetooth.viewmodels.DeviceViewModel;
import com.jancar.bluetooth.viewmodels.PhoneViewModel;
import com.jancar.btservice.bluetooth.IBluetoothExecCallback;

import java.util.List;

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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_phone, container, false);
        initView(rootView);
        init();
        phoneViewModel.getCallNumber().observe(this, s -> callNum.setText(s));
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
        callNum = rootView.findViewById(R.id.tv_call_number);
    }

    private void init() {
        phoneViewModel = new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory()).get(PhoneViewModel.class);
        for(int i = 0; i < btnCount; i++) {
            int finalI = i;
            num[i].setOnClickListener(v -> {
                setCallNum(finalI + "");
            });
        }
        numaBtn.setOnClickListener(v -> setCallNum("*"));
        numbBtn.setOnClickListener(v -> setCallNum("#"));
        cancelBtn.setOnClickListener(v -> {
            String s = phoneViewModel.getCallNumber().getValue();
            int len = s.length();
            phoneViewModel.setCallNumber(s.substring(0, len - 1));
        });
        callBtn.setOnClickListener(v -> {
            String s = phoneViewModel.getCallNumber().getValue();
            MainApplication.getInstance().getBluetoothManager().callPhone(s, new IBluetoothExecCallback.Stub() {
                @Override
                public void onSuccess(String s) {
                    Log.i(TAG, s);
                }

                @Override
                public void onFailure(int i) {
                    Log.i(TAG, i + "");
                }
            });
        });
    }

    private void setCallNum(String s) {
        String callNum = phoneViewModel.getCallNumber().getValue();
        int len = callNum.length();
        int maxLen = 25;
        if (len >= maxLen) {
            return;
        }
        callNum += s;
        phoneViewModel.setCallNumber(callNum);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}