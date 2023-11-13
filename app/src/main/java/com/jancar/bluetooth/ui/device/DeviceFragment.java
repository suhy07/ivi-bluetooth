package com.jancar.bluetooth.ui.device;

import android.Manifest;
import android.arch.lifecycle.ViewModelProvider;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.adapters.DeviceAdapter;
import android.bluetooth.BluetoothDevice;

import com.jancar.bluetooth.broadcast.BluetoothStateReceiver;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.service.BluetoothService;
import com.jancar.bluetooth.utils.BluetoothUtil;
import com.jancar.bluetooth.viewmodels.AddressViewModel;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author suhy
 */
public class DeviceFragment extends Fragment {

    private final static String TAG = "DeviceFragment";
    private RecyclerView recyclerView;
    private Switch bluetoothSwitch;
    private Button renameBtn, scanBtn;
    private ProgressBar scanPb;
    private TextView nameTv;
    private int timeout = 12000;
    private DeviceAdapter deviceAdapter;
    private DeviceViewModel deviceViewModel;
    private Set<BluetoothDevice> deviceSet = new HashSet<>();
    private Map<BluetoothDevice, Integer> connMap = new HashMap<>();
    private BluetoothManager bluetoothManager;
    private com.jancar.sdk.bluetooth.BluetoothManager jancarBluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        initView(view);
        init();
        bluetoothManager = getActivity().getSystemService(BluetoothManager.class);
        jancarBluetoothManager = MainApplication.getInstance().getBluetoothManager();
        bluetoothAdapter = bluetoothManager.getAdapter();
        // 观察设备列表的变化
        deviceViewModel.getDeviceSet().observe(getViewLifecycleOwner(), devices -> {
            Log.d(TAG,"观察到devices列表变化");
            deviceAdapter.setDeviceSet(devices);
            connMap.clear();
            for (BluetoothDevice device : devices) {
                if(device.isConnected())
                    Global.connStatus = Global.CONNECTED;
                connMap.put(device, device.isConnected() ?
                        Global.CONNECTED : Global.NOT_CONNECTED);
            }
            deviceViewModel.setConnMap(connMap);
            deviceAdapter.notifyDataSetChanged();
        });
        deviceViewModel.getConnMap().observe(getViewLifecycleOwner(), connMap -> {
            Log.d(TAG,"观察到connMap变化");
            deviceAdapter.setConnMap(connMap);
            deviceAdapter.notifyDataSetChanged();
        });
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //获取已配对的设备
        deviceViewModel.setDeviceSet(BluetoothUtil.getBondedDevices());
        deviceViewModel.getOnOff().observe(getViewLifecycleOwner(), onOff -> {
            bluetoothSwitch.setChecked(onOff);
            if (onOff) {
                bluetoothAdapter.enable();
                jancarBluetoothManager.powerOn();
                renameBtn.setEnabled(true);
                renameBtn.setText(getText(R.string.bluetooth_rename));
                scanBtn.setEnabled(true);
            } else {
                deviceViewModel.setDeviceSet(new HashSet<>());
                renameBtn.setEnabled(false);
                renameBtn.setText(getText(R.string.bluetooth_rename));
                scanBtn.setEnabled(false);
                nameTv.setEnabled(false);
                scanPb.setVisibility(View.INVISIBLE);
                bluetoothAdapter.disable();
                jancarBluetoothManager.powerOff();
            }
        });
        deviceViewModel.getBluetoothName().observe(getViewLifecycleOwner(), bluetoothName-> {
            nameTv.setText(bluetoothName);
            bluetoothAdapter.setName(bluetoothName);
        });
        deviceViewModel.setOnOff(bluetoothAdapter.isEnabled());
        deviceViewModel.setBluetoothName(bluetoothAdapter.getName());
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
            if (!bluetoothAdapter.isEnabled()){
                bluetoothAdapter.enable();
            }
            bluetoothAdapter.startDiscovery();
            scanPb.setVisibility(View.VISIBLE);
            new Thread(()->{
                try {
                    Thread.sleep(timeout);
                    getActivity().runOnUiThread(()->{
                        scanPb.setVisibility(View.INVISIBLE);
                    });
                } catch (InterruptedException e) {
                    Log.i(TAG, e.getMessage());
                }
            }).start();
        });
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
        // 初始化 ViewModel
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        deviceAdapter = new DeviceAdapter(deviceSet, connMap, deviceViewModel);
        recyclerView.setAdapter(deviceAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    public void setDeviceViewModel(DeviceViewModel deviceViewModel) {
        this.deviceViewModel = deviceViewModel;
    }
}