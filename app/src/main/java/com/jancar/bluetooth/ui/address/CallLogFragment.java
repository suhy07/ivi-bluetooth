package com.jancar.bluetooth.ui.address;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jancar.bluetooth.R;
import com.jancar.bluetooth.adapters.CallLogAdapter;
import com.jancar.bluetooth.model.CallLog;
import com.jancar.bluetooth.viewmodels.AddressViewModel;

import java.util.ArrayList;
import java.util.List;


/**
 * @author suhy
 */
public class CallLogFragment extends Fragment {
    private CallLogAdapter callLogAdapter;
    private RecyclerView recyclerView;
    private List<CallLog> logList = new ArrayList<>();
    private AddressViewModel addressViewModel;

    public CallLogFragment(AddressViewModel addressViewModel) {
        this.addressViewModel = addressViewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_call_log, container, false);
        recyclerView = rootView.findViewById(R.id.rv_call_log);
        callLogAdapter = new CallLogAdapter(logList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(callLogAdapter);
        addressViewModel.getCallLogList().observe(getViewLifecycleOwner(), callLogs -> {
            callLogAdapter.setCallLogs(callLogs);
            callLogAdapter.notifyDataSetChanged();
        });
        return rootView;
    }


    public void setAddressViewModel(AddressViewModel addressViewModel) {
        this.addressViewModel = addressViewModel;
    }
}
