package com.jancar.bluetooth.ui.device;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jancar.bluetooth.R;
import com.jancar.bluetooth.adapters.DeviceAdapter;
import android.bluetooth.BluetoothDevice;
import com.jancar.bluetooth.service.BluetoothScanService;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;


import java.util.HashSet;
import java.util.Set;

/**
 * @author suhy
 */
public class DeviceFragment extends Fragment {

    private RecyclerView recyclerView;
    private Switch bluetoothSwitch;
    private Button renameBtn, scanBtn;
    private ProgressBar scanPb;
    private TextView nameTv;
    private int timeout = 12000;
    private DeviceAdapter deviceAdapter;
    private DeviceViewModel deviceViewModel;
    private Set<BluetoothDevice> deviceList = new HashSet<>();
    private BluetoothScanService scanService;
    private ServiceConnection serviceConnection;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        initView(view);
        init();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_CONNECT) !=
                PackageManager.PERMISSION_GRANTED) {
            return view;
        }
        bluetoothManager = getActivity().getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
        // 观察设备列表的变化
        deviceViewModel.getDeviceList().observe(getViewLifecycleOwner(), devices -> {
            deviceAdapter.setDeviceSet(devices);
            deviceAdapter.notifyDataSetChanged();
        });
        deviceViewModel.getOnOff().observe(getViewLifecycleOwner(), onOff -> {
            bluetoothSwitch.setChecked(onOff);
            if (onOff) {
                bluetoothAdapter.enable();
            } else {
                bluetoothAdapter.disable();
            }
        });
        deviceViewModel.getBluetoothName().observe(getViewLifecycleOwner(), bluetoothName-> {
            nameTv.setText(bluetoothName);
            bluetoothAdapter.setName(bluetoothName);
        });
        deviceViewModel.setOnOff(bluetoothAdapter.isEnabled());
        bluetoothSwitch.setOnCheckedChangeListener((v, b) -> deviceViewModel.setOnOff(b));
        renameBtn.setOnClickListener(v->{
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            }
            if (nameTv.isEnabled()){
                renameBtn.setText(getText(R.string.bluetooth_rename));
                nameTv.setEnabled(false);
                deviceViewModel.setBluetoothName(nameTv.getText() + "");
            } else {
                renameBtn.setText(getText(R.string.str_finish));
                nameTv.setEnabled(true);
            }
        });
        scanBtn.setOnClickListener(v -> {
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            }
            if (scanService != null) {
                scanService.startScan();
                scanPb.setVisibility(View.VISIBLE);
                new Thread(()->{
                    try {
                        Thread.sleep(timeout);
                        getActivity().runOnUiThread(()->{
                            scanPb.setVisibility(View.INVISIBLE);
                        });
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        });
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.d("bluetoothAdapter", "Device doesn't support Bluetooth");
            return view;
        }
        deviceViewModel.setBluetoothName(bluetoothAdapter.getName());
        deviceViewModel.setOnOff(bluetoothAdapter.isEnabled());
        return view;
    }
    private void initView(View view){
        recyclerView = view.findViewById(R.id.rv_bluetooth_devices);
        bluetoothSwitch = view.findViewById(R.id.switch_bluetooth);
        renameBtn = view.findViewById(R.id.btn_bluetooth_name);
        scanBtn = view.findViewById(R.id.btn_bluetooth_scan);
        nameTv = view.findViewById(R.id.tv_bluetooth_name);
        scanPb = view.findViewById(R.id.pb_scan);
    }

    private void init(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        deviceAdapter = new DeviceAdapter(deviceList);
        recyclerView.setAdapter(deviceAdapter);
        // 初始化 ViewModel
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        // 初始化 Service
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.d("onServiceConnected","onServiceConnected");
                BluetoothScanService.LocalBinder binder = (BluetoothScanService.LocalBinder) iBinder;
                scanService = binder.getService();
                scanService.setDeviceViewModel(deviceViewModel);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                scanService = null;
            }
        };
        Intent serviceIntent = new Intent(getActivity(), BluetoothScanService.class);
        // 启动服务
        getActivity().startService(serviceIntent);
        getActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void setBluetoothManager(BluetoothManager bluetoothManager) {
        this.bluetoothManager = bluetoothManager;
    }

    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }
}