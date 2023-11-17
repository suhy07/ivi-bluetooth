package com.jancar.bluetooth.ui.address;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.adapters.CallLogAdapter;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.model.CallLog;
import com.jancar.bluetooth.utils.TimeUtil;
import com.jancar.bluetooth.viewmodels.AddressViewModel;
import com.jancar.btservice.bluetooth.BluetoothVCardBook;
import com.jancar.btservice.bluetooth.IBluetoothExecCallback;
import com.jancar.btservice.bluetooth.IBluetoothVCardCallback;
import com.jancar.sdk.bluetooth.BluetoothManager;

import java.util.ArrayList;
import java.util.List;


/**
 * @author suhy
 */
public class CallLogFragment extends Fragment {
    private final static String TAG = "CallLogFragment";
    private CallLogAdapter callLogAdapter;
    private RecyclerView recyclerView;
    private ImageButton refreshBtn;
    private ProgressBar callLogPb;
    private AddressViewModel addressViewModel;
    private BluetoothManager bluetoothManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_call_log, container, false);
        initView(rootView);
        init();
        if (addressViewModel != null) {
            addressViewModel.getCallLogList().observe(getViewLifecycleOwner(), callLogs -> {
                Log.d(TAG, "观察到calllog变化");
                callLogAdapter.setCallLogs(callLogs);
                callLogPb.setVisibility(View.GONE);
                callLogAdapter.notifyDataSetChanged();
            });
        }
        refreshBtn.setOnClickListener(v -> {
            if (Global.connStatus == Global.NOT_CONNECTED) {
                MainApplication.showToast(getString(R.string.str_not_connect_warn));
            } else {
                searchCallLog();
            }
        });
        return rootView;
    }

    private void initView(View rootView) {
        recyclerView = rootView.findViewById(R.id.rv_call_log);
        refreshBtn = rootView.findViewById(R.id.btn_refresh_call_log);
        callLogPb = rootView.findViewById(R.id.pb_call_log);
    }
    private void init(){
        bluetoothManager = MainApplication.getInstance().getBluetoothManager();
        callLogAdapter = new CallLogAdapter(addressViewModel.getCallLogList().getValue());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(callLogAdapter);
    }

    public IBluetoothVCardCallback.Stub stub = new IBluetoothVCardCallback.Stub() {
        @Override
        public void onProgress(List<BluetoothVCardBook> list) {
            List<CallLog> callLogs = new ArrayList<>();
            for (BluetoothVCardBook book: list) {
                callLogs.add(new CallLog(book.name, TimeUtil.formatAccurateTime(book.callTime), book.phoneNumber, book.type));
            }
            if (addressViewModel != null) {
                addressViewModel.setCallLogList(callLogs);
            }
            callLogPb.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(int i) {
            callLogPb.setVisibility(View.GONE);
        }

        @Override
        public void onSuccess(String s) {
            callLogPb.setVisibility(View.GONE);
        }
    };

    public void setAddressViewModel(AddressViewModel addressViewModel) {
        this.addressViewModel = addressViewModel;
    }

    private void searchCallLog() {
        if(Global.connStatus != Global.CONNECTED) {
            return;
        }
//        bluetoothManager.stopContactOrHistoryLoad(stub1);
        if(bluetoothManager != null) {
            bluetoothManager.getAllCallRecord(stub);
            callLogPb.setVisibility(View.VISIBLE);
        }
        //超时
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Global.connStatus == Global.NOT_CONNECTED) {
            addressViewModel.setCallLogList(new ArrayList<>());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (addressViewModel != null) {
                List<CallLog> callLogs = addressViewModel.getCallLogList().getValue();
                if (callLogs != null && callLogs.isEmpty()) {
                    searchCallLog();
                }
                if (Global.connStatus == Global.NOT_CONNECTED) {
                    addressViewModel.setCallLogList(new ArrayList<>());
                }
            }
        }
    }

}
