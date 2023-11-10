package com.jancar.bluetooth.ui.address;

import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.adapters.CallLogAdapter;
import com.jancar.bluetooth.model.CallLog;
import com.jancar.bluetooth.model.Contact;
import com.jancar.bluetooth.utils.TimeUtil;
import com.jancar.bluetooth.viewmodels.AddressViewModel;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;
import com.jancar.btservice.bluetooth.BluetoothVCardBook;
import com.jancar.btservice.bluetooth.IBluetoothExecCallback;
import com.jancar.btservice.bluetooth.IBluetoothVCardCallback;
import com.jancar.sdk.bluetooth.BluetoothManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author suhy
 */
public class CallLogFragment extends Fragment {
    private final static String TAG = "CallLogFragment";
    private CallLogAdapter callLogAdapter;
    private RecyclerView recyclerView;
    private Button refreshBtn;
    private ProgressBar callLogPb;
    private List<CallLog> logList = new ArrayList<>();
    private AddressViewModel addressViewModel;
    private BluetoothManager bluetoothManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_call_log, container, false);
        initView(rootView);
        init();
        addressViewModel.getCallLogList().observe(getViewLifecycleOwner(), callLogs -> {
            Log.d(TAG, "观察到calllog变化");
            callLogAdapter.setCallLogs(callLogs);
            callLogPb.setVisibility(View.GONE);
            callLogAdapter.notifyDataSetChanged();
        });
        refreshBtn.setOnClickListener(v -> {
            bluetoothManager.openBluetoothModule(stub1);
            bluetoothManager.stopContactOrHistoryLoad(stub1);
            bluetoothManager.getAllCallRecord(stub);
            callLogPb.setVisibility(View.VISIBLE);
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
        callLogAdapter = new CallLogAdapter(logList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(callLogAdapter);
    }

    IBluetoothVCardCallback.Stub stub = new IBluetoothVCardCallback.Stub() {
        @Override
        public void onProgress(List<BluetoothVCardBook> list) {
            List<CallLog> callLogs = new ArrayList<>();
            for (BluetoothVCardBook book: list) {
                callLogs.add(new CallLog( book.name, TimeUtil.formatTime(book.callTime), book.phoneNumber));
            }
            addressViewModel.setCallLogList(callLogs);
        }

        @Override
        public void onFailure(int i) {

        }

        @Override
        public void onSuccess(String s) {

        }
    };
    private IBluetoothExecCallback.Stub stub1 = new IBluetoothExecCallback.Stub() {
        @Override
        public void onFailure(int i) {
            Log.d(TAG, i + "");
        }

        @Override
        public void onSuccess(String s) {
            Log.d(TAG, s + "");
        }

    };

    public void setAddressViewModel(AddressViewModel addressViewModel) {
        this.addressViewModel = addressViewModel;
    }
}
