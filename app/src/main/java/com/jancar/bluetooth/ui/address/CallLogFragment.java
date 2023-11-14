package com.jancar.bluetooth.ui.address;

import android.app.Activity;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.widget.TextView;

import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.adapters.CallLogAdapter;
import com.jancar.bluetooth.global.Global;
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
    private static TextView totalTv;
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
            totalTv.setText(MainApplication.getInstance().getString(R.string.str_contact_total1) + " " +
                    callLogs.size() + " " + MainApplication.getInstance().getString(R.string.str_contact_total2));
            callLogAdapter.notifyDataSetChanged();
        });
        refreshBtn.setOnClickListener(v -> {
            searchCallLog();
        });
        return rootView;
    }

    private void initView(View rootView) {
        recyclerView = rootView.findViewById(R.id.rv_call_log);
        refreshBtn = rootView.findViewById(R.id.btn_refresh_call_log);
        callLogPb = rootView.findViewById(R.id.pb_call_log);
        totalTv = rootView.findViewById(R.id.tv_call_total);
    }
    private void init(){
        bluetoothManager = MainApplication.getInstance().getBluetoothManager();
        callLogAdapter = new CallLogAdapter(logList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(callLogAdapter);
    }

    public IBluetoothVCardCallback.Stub stub = new IBluetoothVCardCallback.Stub() {
        @Override
        public void onProgress(List<BluetoothVCardBook> list) {
            List<CallLog> callLogs = new ArrayList<>();
            for (BluetoothVCardBook book: list) {
                callLogs.add(new CallLog(book.name, TimeUtil.formatAccurateTime(book.callTime), book.phoneNumber));
//                Log.i(TAG, "Type:" + book.type + " " + book.phoneNumber );
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

    private void searchCallLog() {
        if(Global.connStatus != Global.CONNECTED) {
            MainApplication.showToast(getString(R.string.str_not_connect_warn));
            return;
        }
        bluetoothManager.stopContactOrHistoryLoad(stub1);
        bluetoothManager.getAllCallRecord(stub);
        callLogPb.setVisibility(View.VISIBLE);
        //超时
        new Thread(()-> {
            try {
                Thread.sleep(Global.TIMEOUT);
            } catch (InterruptedException e) {
                Log.i(TAG, e.getMessage());
            }
            getActivity().runOnUiThread(()->{
                callLogPb.setVisibility(View.GONE);
            });
        }).start();
    }

    private static boolean isFirst = true;
    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        if(addressViewModel.getCallLogList().getValue().isEmpty()){
            callLogPb.setVisibility(View.VISIBLE);
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Log.i(TAG, e.getMessage());
                }
                Activity activity = getActivity();
                if(activity!=null && !activity.isFinishing()){
                    activity.runOnUiThread(()-> {
                        searchCallLog();
                        callLogPb.setVisibility(View.INVISIBLE);
                    });
                }
                getActivity().runOnUiThread(this::searchCallLog);
            }).start();
        }
    }
}
