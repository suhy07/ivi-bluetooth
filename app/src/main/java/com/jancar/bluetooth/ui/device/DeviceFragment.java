package com.jancar.bluetooth.ui.device;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.adapters.DeviceAdapter;
import com.jancar.bluetooth.broadcast.BluetoothConnectionReceiver;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.ui.MyLinearLayoutManager;
import com.jancar.bluetooth.utils.BluetoothUtil;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;
import com.jancar.sdk.bluetooth.IVIBluetooth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author suhy
 */
public class DeviceFragment extends Fragment {

    private final static String TAG = "DeviceFragment";
    private RecyclerView recyclerView;
    //private Switch bluetoothSwitch;
    private Button renameBtn, scanBtn;
    private ProgressBar scanPb;
    private EditText nameTv;
    private int timeout = 12000;
    private DeviceAdapter deviceAdapter;
    private DeviceViewModel deviceViewModel;
    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private BluetoothManager bluetoothManager;
    private com.jancar.sdk.bluetooth.BluetoothManager jancarBluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private final static int SWITCH_TIMEOUT = 2000;
    private final static int SCAN_TIMEOUT = 15000;
    private final static int SWITCH_WHAT = 0;
    private final static int SCAN_WHAT = 1;
    private final mHandler mHandler = new mHandler();
    private boolean isFirstOpen = true;
    private int beforeSize = -1;
    private ImageView switchImg;


    @SuppressLint("ClickableViewAccessibility")
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
        if (deviceViewModel != null) {
            deviceViewModel.getDeviceList().observe(getViewLifecycleOwner(), devices -> {
                deviceList = devices;
                if (devices != null) {
                    Log.d(TAG, "观察到devices列表变化");
                    /*Global.connStatus = Global.NOT_CONNECTED;
                    for (BluetoothDevice device : devices) {
                        if (device.isConnected()) {
                            Log.i(TAG, "监测到设备已连接");
                            Global.connStatus = Global.CONNECTED;
                            break;
                        }
                    }*/
                    deviceAdapter.setDeviceList(devices);
                    if(BluetoothConnectionReceiver.needFresh){
                        BluetoothConnectionReceiver.needFresh = false;
                        deviceAdapter.notifyDataSetChanged();
                    }
                    int nowSize = devices.size();
                    if (nowSize > beforeSize) {
                        Log.d(TAG, "共计增加:" + (nowSize - beforeSize));
                        for (int i = nowSize - beforeSize; i > 0 ; i--) {
                            deviceAdapter.notifyItemInserted(nowSize - i);
                            Log.d(TAG, "增加设备:" + i);
                        }
                    }else if (beforeSize != devices.size()) {
                        deviceAdapter.sortDeviceList(devices);
                    } else {
                        deviceAdapter.sortDeviceListNoScroll(devices);
                    }
                    beforeSize = devices.size();
//                    deviceAdapter.sortDeviceList(devices);
                }
            });
            //获取已配对的设备
            Set<BluetoothDevice> bondDevice = BluetoothUtil.getBondedDevices();
            Log.i(TAG, "已配对的设备：" + Arrays.toString(bondDevice.toArray()));
            deviceList.addAll(bondDevice);
            deviceViewModel.setDeviceList(deviceList);
            deviceAdapter.sortDeviceList(deviceList);
            deviceAdapter.setmStartPairOrConnectCallback(new DeviceAdapter.StartPairOrConnectCallback() {
                @Override
                public void startPairOrConnect() {
                    if(bluetoothAdapter!=null){
                        if(bluetoothAdapter.isDiscovering()){
                            bluetoothAdapter.cancelDiscovery();
                        }
                        scanPb.setVisibility(View.INVISIBLE);
                        mHandler.removeMessages(SCAN_WHAT);
                    }

                }
            });
            deviceViewModel.getBluetoothName().observe(getViewLifecycleOwner(), bluetoothName -> {
                if (!"".equals(bluetoothName)) {
                    nameTv.setText(bluetoothName);
                    bluetoothAdapter.setName(bluetoothName);
                }
            });
            deviceViewModel.setBluetoothName(bluetoothAdapter.getName());
            deviceViewModel.getOnOff().observe(getViewLifecycleOwner(), onOff -> {
                switchImg.setSelected(onOff);
                if (onOff) {
                    /*boolean res = bluetoothAdapter.enable();
                    if (res) {
                        bluetoothSwitch.setChecked(true);
                    }*/
                    //bluetoothSwitch.setChecked(true);
                    recyclerView.setVisibility(View.VISIBLE);
                    deviceAdapter.sortDeviceList(deviceList);
                } else {
                    /*boolean res = bluetoothAdapter.disable();
                    if (res) {
                        bluetoothSwitch.setChecked(false);
                    }*/
                    recyclerView.setVisibility(View.GONE);
                    renameBtn.setEnabled(false);
                    renameBtn.setText(getText(R.string.bluetooth_rename));
                    scanBtn.setEnabled(false);
                    nameTv.setEnabled(false);
                    scanPb.setVisibility(View.INVISIBLE);
                    //bluetoothSwitch.setChecked(false);
                    deviceAdapter.sortDeviceList(new ArrayList<>());
                    Global.connStatus = Global.NOT_CONNECTED;
                }
                nameTv.setText(deviceViewModel.getBluetoothName().getValue());
            });
            deviceViewModel.setOnOff(bluetoothAdapter.isEnabled());
            switchImg.setSelected(bluetoothAdapter.isEnabled());

        }
        switchImg.setOnClickListener( v -> {
            switchImg.setEnabled(false);
            if(bluetoothAdapter.isEnabled()){
                bluetoothAdapter.disable();
            }else{
                bluetoothAdapter.enable();
            }
            /*boolean b = !bluetoothAdapter.isEnabled();
            bluetoothSwitch.setChecked(!b);
            if (deviceViewModel != null) {
                deviceViewModel.setOnOff(b);
                bluetoothSwitch.setEnabled(false);
                new Thread(()-> {
                    Message msg = Message.obtain();
                    msg.what = SWITCH_WHAT;
                    mHandler.sendMessageDelayed(msg, SWITCH_TIMEOUT);
                }).start();
            }*/
        });

        renameBtn.setOnClickListener(v -> {
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            }
            Log.i(TAG, nameTv.isEnabled() + "");
            if (nameTv.isEnabled()) {
                renameBtn.setText(getText(R.string.bluetooth_rename));
                nameTv.setEnabled(false);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(renameBtn.getWindowToken(), 0);
                if (deviceViewModel != null) {
                    if (nameTv.getText().toString().trim().equals("")) {
                        nameTv.setText(deviceViewModel.getBluetoothName().getValue());
                    } else {
                        deviceViewModel.setBluetoothName(nameTv.getText() + "");
                    }
                }
            } else {
                renameBtn.setText(getText(R.string.str_finish));
                nameTv.setEnabled(true);
                nameTv.requestFocus();
                nameTv.setSelection(nameTv.getText().length());
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        scanBtn.setOnClickListener(v -> {
            searchDevice();
            hideKeyboard(v);
        });
        nameTv.setImeOptions(EditorInfo.IME_ACTION_DONE);
        nameTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // 文本变化前的回调
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // 文本变化中的回调
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 文本变化后的回调
                // 获取当前文本长度
                int textLength = editable.length();
                int maxLength = 21;
                if (textLength > maxLength) {
                    // 如果超过限制，截取前面的限制字符
                    editable.delete(maxLength, textLength);
                }
            }
        });
        nameTv.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                renameBtn.setText(getText(R.string.bluetooth_rename));
                nameTv.setEnabled(false);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(renameBtn.getWindowToken(), 0);
                if (deviceViewModel != null) {
                    if (nameTv.getText().toString().trim().equals("")) {
                        nameTv.setText(deviceViewModel.getBluetoothName().getValue());
                    } else {
                        deviceViewModel.setBluetoothName(nameTv.getText() + "");
                    }
                }
                return true;
            }
            return false;
        });
        view.setOnClickListener(this::hideKeyboard);
        view.setOnTouchListener((v, event) -> {
            hideKeyboard(v);
            return false;
        });
        if (bluetoothAdapter.isEnabled()) {
            if (isFirstOpen) {
                isFirstOpen = false;
                searchDevice();
            }
        }
        return view;
    }

    private void searchDevice() {
        Log.i(TAG, "扫描设备");
//        bluetoothAdapter.cancelDiscovery();
        if(bluetoothAdapter.isDiscovering()){
            return;
        }
        Global.scanStatus = Global.SCANNING;
        scanPb.setVisibility(View.VISIBLE);


        bluetoothAdapter.startDiscovery();
        mHandler.removeMessages(SCAN_WHAT);
        mHandler.sendEmptyMessageDelayed(SCAN_WHAT,SCAN_TIMEOUT);
        /*new Thread(() -> {
            boolean flag = bluetoothAdapter.startDiscovery();

            Log.i(TAG, "Discovery:" + flag);
            Message msg = Message.obtain();
            msg.what = SCAN_WHAT;
            mHandler.sendMessageDelayed(msg, SCAN_TIMEOUT);
        }).start();*/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventPowerStatusChanged(IVIBluetooth.EventPowerState event) {
        int state = bluetoothAdapter.getState();
        Log.i(TAG, "onEventPowerStatusChanged state:" + state);
        if (state == BluetoothAdapter.STATE_ON) {
            renameBtn.setEnabled(true);
            renameBtn.setText(getText(R.string.bluetooth_rename));
            scanBtn.setEnabled(true);
            switchImg.setEnabled(true);
            switchImg.setSelected(true);
            searchDevice();
        } else if (state == BluetoothAdapter.STATE_OFF){
            deviceViewModel.setDeviceList(new ArrayList<>());
            renameBtn.setEnabled(false);
            renameBtn.setText(getText(R.string.bluetooth_rename));
            scanBtn.setEnabled(false);
            nameTv.setEnabled(false);
            scanPb.setVisibility(View.INVISIBLE);
            switchImg.setEnabled(true);
            switchImg.setSelected(false);
            if(bluetoothAdapter.isDiscovering()){
                bluetoothAdapter.cancelDiscovery();
            }
            mHandler.removeMessages(SCAN_WHAT);
        }else if (state == BluetoothAdapter.STATE_TURNING_ON || state == BluetoothAdapter.STATE_TURNING_OFF){
            switchImg.setEnabled(false);
        }
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.rv_bluetooth_devices);
        //bluetoothSwitch = view.findViewById(R.id.switch_bluetooth);
        renameBtn = view.findViewById(R.id.btn_bluetooth_name);
        scanBtn = view.findViewById(R.id.btn_bluetooth_scan);
        nameTv = view.findViewById(R.id.tv_bluetooth_name);
        scanPb = view.findViewById(R.id.pb_scan);
        switchImg = view.findViewById(R.id.switchImg);
    }

    private void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        recyclerView.setLayoutManager(new MyLinearLayoutManager(getContext()));
        deviceAdapter = new DeviceAdapter(deviceList, deviceViewModel, recyclerView);
        recyclerView.setAdapter(deviceAdapter);
        recyclerView.setItemViewCacheSize(20);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");
        deviceAdapter.removeHandler();
        bluetoothAdapter.cancelDiscovery();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(switchImg.isSelected()){
            deviceAdapter.notifyDataSetChanged();
        }

    }

    public void setDeviceViewModel(DeviceViewModel deviceViewModel) {
        this.deviceViewModel = deviceViewModel;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    class mHandler extends Handler {
        //重写handleMessage（）方法
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //执行的UI操作
            switch (msg.what) {
                case SWITCH_WHAT:
                    //bluetoothSwitch.setEnabled(true);
                    break;
                case SCAN_WHAT:
                    bluetoothAdapter.cancelDiscovery();
                    scanPb.setVisibility(View.GONE);
                    Global.scanStatus = Global.NOT_SCAN;
                    break;
                default:
                    break;
            }
        }
    }
}