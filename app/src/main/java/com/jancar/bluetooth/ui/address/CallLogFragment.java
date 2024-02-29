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
import com.jancar.bluetooth.model.CallLog;
import com.jancar.bluetooth.model.Contact;
import com.jancar.bluetooth.utils.CallUtil;
import com.jancar.bluetooth.utils.TimeUtil;
import com.jancar.bluetooth.viewmodels.AddressViewModel;
import com.jancar.btservice.bluetooth.BluetoothVCardBook;
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
    private boolean isSearching = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_call_log, container, false);
        Log.i(TAG, "onCreateView");
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
            searchCallLog();
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
       List<CallLog> callLogs = new ArrayList<>();
        @Override
        public void onProgress(List<BluetoothVCardBook> list) {
            callLogs = new ArrayList<>();
            for (BluetoothVCardBook book: list) {
                callLogs.add(new CallLog(book.name, TimeUtil.formatAccurateTime(book.callTime), book.phoneNumber, book.type));
            }
        }

        @Override
        public void onFailure(int i) {
            callLogPb.setVisibility(View.GONE);
            Log.i(TAG, "onFailure");
            isSearching = false;
        }

        @Override
        public void onSuccess(String s) {
            Log.i(TAG, "onSuccess");
            if (addressViewModel != null) {
                Log.i(TAG, "addressViewModel.setCallLogList(callLogs)");
                addressViewModel.setCallLogList(callLogs);
                Log.i(TAG, callLogs.toString());
            }
            callLogPb.setVisibility(View.GONE);
            isSearching = false;
        }
    };

    public IBluetoothVCardCallback.Stub stub1 = new IBluetoothVCardCallback.Stub() {
        @Override
        public void onProgress(List<BluetoothVCardBook> list) {
            List<Contact> contacts = new ArrayList<>();
            for (BluetoothVCardBook book: list) {
                Contact contact = new Contact(book.name, book.phoneNumber);
                contacts.add(contact);
            }
            if (addressViewModel != null) {
                addressViewModel.setContactList(contacts);
            }
        }

        @Override
        public void onFailure(int i) {

        }

        @Override
        public void onSuccess(String s) {

        }


    };

    public void setAddressViewModel(AddressViewModel addressViewModel) {
        this.addressViewModel = addressViewModel;
    }

    private void searchCallLog() {
        if (isSearching) {
            return;
        }
        Log.i(TAG, "searchCallLog");
        if (!CallUtil.getInstance().canCallNumber()) {
            MainApplication.showToast(getString(R.string.str_not_connect_warn));
        }
        else {
            if (bluetoothManager != null) {
                isSearching = true;
//                bluetoothManager.getPhoneContacts(stub1);
                bluetoothManager.getAllCallRecord(stub);
                callLogPb.setVisibility(View.VISIBLE);
            }
        }
        //超时
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        isSearching = false;
        if (!CallUtil.getInstance().canCallNumber()) {
            addressViewModel.setCallLogList(new ArrayList<>());
            callLogPb.setVisibility(View.GONE);
        }
        if (addressViewModel != null) {
            List<Contact> contacts = addressViewModel.getContactList().getValue();
            if (contacts != null && contacts.isEmpty()) {
                searchCallLog();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            if (addressViewModel != null) {
//                List<CallLog> callLogs = addressViewModel.getCallLogList().getValue();
//                if (callLogs != null && callLogs.isEmpty()) {
//                    searchCallLog();
//                }
//                if (!CallUtil.getInstance().canCallNumber()) {
//                    addressViewModel.setCallLogList(new ArrayList<>());
//                }
//            }
//        }
    }

}
