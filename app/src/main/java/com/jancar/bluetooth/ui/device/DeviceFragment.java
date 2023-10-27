package com.jancar.bluetooth.ui.device;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jancar.bluetooth.R;
import com.jancar.bluetooth.adapters.DeviceAdapter;
import com.jancar.bluetooth.model.BluetoothDevice;


import java.util.ArrayList;
import java.util.List;

/**
 * @author suhy
 */
public class DeviceFragment extends Fragment {

    private RecyclerView recyclerView;
    private DeviceAdapter deviceAdapter;
    private List<BluetoothDevice> deviceList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);

        recyclerView = view.findViewById(R.id.rv_bluetooth_devices);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        deviceAdapter = new DeviceAdapter(deviceList);
        recyclerView.setAdapter(deviceAdapter);

        // TODO 在此添加扫描蓝牙设备的逻辑，并将扫描到的设备添加到 deviceList

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}