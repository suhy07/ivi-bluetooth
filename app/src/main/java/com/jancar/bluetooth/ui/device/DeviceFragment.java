package com.jancar.bluetooth.ui.device;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.jancar.bluetooth.BR;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.app.BluetoothApplication;
import com.jancar.bluetooth.databinding.FragmentDeviceBinding;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.ui.MyLinearLayoutManager;
import com.jancar.sdk.bluetooth.IVIBluetooth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;

/**
 * @author suhy
 */
public class DeviceFragment extends BaseFragment<FragmentDeviceBinding, DeviceViewModel> {

    private final static String TAG = "DeviceFragment";
    private RecyclerView recyclerView;
    private Button renameBtn, scanBtn;
    private ProgressBar scanPb;
    private EditText nameTv;
    private DeviceAdapter deviceAdapter;
    private DeviceViewModel deviceViewModel;
    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private BluetoothManager bluetoothManager;
    private com.jancar.sdk.bluetooth.BluetoothManager jancarBluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private final static int SCAN_TIMEOUT = 10000;
    private final static int SWITCH_WHAT = 0;
    private final static int SCAN_WHAT = 1;
    private final mHandler mHandler = new mHandler();
    private boolean isFirstOpen = true;
    private int beforeSize = -1;
    private ImageView switchImg;
    private BluetoothPairReceiver pairReceiver;
    private BluetoothConnectionReceiver_ connectReceiver;


    @Override
    public void initData() {
        super.initData();
        //给RecyclerView添加Adpter，请使用自定义的Adapter继承BindingRecyclerViewAdapter，重写onBindBinding方法，里面有你要的Item对应的binding对象。
        // Adapter属于View层的东西, 不建议定义到ViewModel中绑定，以免内存泄漏
        binding.setAdapter(new DeviceAdapter());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        View view = binding.getRoot();
//        initView(view);
//        init();

        bluetoothManager = getActivity().getSystemService(BluetoothManager.class);
        jancarBluetoothManager = BluetoothApplication.getInstance().getBluetoothManager();
        bluetoothAdapter = bluetoothManager.getAdapter();
//        if (CallUtil.getInstance().isConnected()) {
//            jancarBluetoothManager.playBtMusic(null);
//            jancarBluetoothManager.pauseBtMusic(null);
//        }
        // 观察设备列表的变化
//        if (deviceViewModel != null) {
//            deviceViewModel.getDeviceList().observe(getViewLifecycleOwner(), devices -> {
//                deviceList = devices;
//                if (devices != null) {
//                    Log.d(TAG, "观察到devices列表变化");
//                    deviceAdapter.setDeviceList(devices);
//                    if(BluetoothConnectionReceiver.needFresh){
//                        BluetoothConnectionReceiver.needFresh = false;
//                        deviceAdapter.notifyDataSetChanged();
//                    }
//                    int nowSize = devices.size();
//                    if (nowSize > beforeSize) {//
//                        Log.d(TAG, "共计增加:" + (nowSize - beforeSize));
//                        for (int i = nowSize - beforeSize; i > 0 ; i--) {
//                            deviceAdapter.notifyItemInserted(nowSize - i);
//                            Log.d(TAG, "增加设备:" + i);
//                        }
//                    } else {
//                        deviceAdapter.sortDeviceList(deviceList);
//                    }
//                    beforeSize = devices.size();
//                }
//            });
//            // 获取已配对的设备
//            Set<BluetoothDevice> bondDevice = BluetoothUtil.getBondedDevices();
//            Log.i(TAG, "已配对的设备：" + Arrays.toString(bondDevice.toArray()));
//            deviceList.addAll(bondDevice);
//            deviceAdapter.sortDeviceList(deviceList);
//            deviceViewModel.setDeviceList(deviceList);
//            deviceAdapter.setmStartPairOrConnectCallback(new DeviceAdapter.StartPairOrConnectCallback() {
//                @Override
//                public void startPairOrConnect() {
//                    if(bluetoothAdapter!=null){
//                        if(bluetoothAdapter.isDiscovering()){
//                            bluetoothAdapter.cancelDiscovery();
//                        }
//                        scanPb.setVisibility(View.INVISIBLE);
//                        mHandler.removeMessages(SCAN_WHAT);
//                    }
//
//                }
//            });
//            deviceViewModel.getBluetoothName().observe(getViewLifecycleOwner(), bluetoothName -> {
//                if (!"".equals(bluetoothName)) {
//                    nameTv.setText(bluetoothName);
//                    bluetoothAdapter.setName(bluetoothName);
//                }
//            });
//            deviceViewModel.setBluetoothName(bluetoothAdapter.getName());
//            deviceViewModel.getOnOff().observe(getViewLifecycleOwner(), onOff -> {
//                switchImg.setSelected(onOff);
//                if (onOff) {
//                    recyclerView.setVisibility(View.VISIBLE);
//                } else {
//                    recyclerView.setVisibility(View.GONE);
//                    renameBtn.setEnabled(false);
//                    renameBtn.setText(getText(R.string.bluetooth_rename));
//                    scanBtn.setEnabled(false);
//                    nameTv.setEnabled(false);
//                    scanPb.setVisibility(View.INVISIBLE);
//                    Global.connStatus = Global.NOT_CONNECTED;
//                }
//                nameTv.setText(deviceViewModel.getBluetoothName().getValue());
//            });
//            deviceViewModel.setOnOff(bluetoothAdapter.isEnabled());
//            switchImg.setSelected(bluetoothAdapter.isEnabled());
//
//        }
//        switchImg.setOnClickListener( v -> {
//            switchImg.setEnabled(false);
//            if(bluetoothAdapter.isEnabled()){
//                bluetoothAdapter.disable();
//            }else{
//                bluetoothAdapter.enable();
//            }
//        });
//
//        renameBtn.setOnClickListener(v -> {
//            if (!bluetoothAdapter.isEnabled()) {
//                bluetoothAdapter.enable();
//            }
//            Log.i(TAG, nameTv.isEnabled() + "");
//            if (nameTv.isEnabled()) {
//                renameBtn.setText(getText(R.string.bluetooth_rename));
//                nameTv.setEnabled(false);
//                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(renameBtn.getWindowToken(), 0);
//                if (deviceViewModel != null) {
//                    if ("".equals(nameTv.getText().toString().trim())) {
//                        nameTv.setText(deviceViewModel.getBluetoothName().getValue());
//                    } else {
//                        deviceViewModel.setBluetoothName(nameTv.getText() + "");
//                    }
//                }
//            } else {
//                renameBtn.setText(getText(R.string.str_finish));
//                nameTv.setEnabled(true);
//                nameTv.requestFocus();
//                nameTv.setSelection(nameTv.getText().length());
//                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
//            }
//        });
//        scanBtn.setOnClickListener(v -> {
//            searchDevice();
//            hideKeyboard(v);
//        });
//        nameTv.setImeOptions(EditorInfo.IME_ACTION_DONE);
//        nameTv.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
//                // 文本变化前的回调
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//                // 文本变化中的回调
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                // 文本变化后的回调
//                // 获取当前文本长度
//                int textLength = editable.length();
//                int maxLength = 21;
//                if (textLength > maxLength) {
//                    // 如果超过限制，截取前面的限制字符
//                    editable.delete(maxLength, textLength);
//                }
//            }
//        });
//        nameTv.setOnEditorActionListener((v, actionId, event) -> {
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                renameBtn.setText(getText(R.string.bluetooth_rename));
//                nameTv.setEnabled(false);
//                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(renameBtn.getWindowToken(), 0);
//                if (deviceViewModel != null) {
//                    if ("".equals(nameTv.getText().toString().trim())) {
//                        nameTv.setText(deviceViewModel.getBluetoothName().getValue());
//                    } else {
//                        deviceViewModel.setBluetoothName(nameTv.getText() + "");
//                    }
//                }
//                return true;
//            }
//            return false;
//        });
//        view.setOnClickListener(this::hideKeyboard);
//        view.setOnTouchListener((v, event) -> {
//            hideKeyboard(v);
//            return false;
//        });
//        if (bluetoothAdapter.isEnabled()) {
//            if (isFirstOpen) {
//                isFirstOpen = false;
//                searchDevice();
//            }
//        }
        return view;
    }

    private void searchDevice() {
        Log.i(TAG, "扫描设备");
        if(bluetoothAdapter.isDiscovering()){
            return;
        }
        Global.scanStatus = Global.SCANNING;
        scanPb.setVisibility(View.VISIBLE);


        bluetoothAdapter.startDiscovery();
        mHandler.removeMessages(SCAN_WHAT);
        mHandler.sendEmptyMessageDelayed(SCAN_WHAT,SCAN_TIMEOUT);
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
//            deviceViewModel.setDeviceList(new ArrayList<>());
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
        deviceAdapter = new DeviceAdapter();
        recyclerView.setAdapter(deviceAdapter);
        recyclerView.setItemViewCacheSize(20);
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);;
        pairReceiver = new BluetoothPairReceiver();
        connectReceiver = new BluetoothConnectionReceiver_();
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        filter2.addAction(BluetoothA2dpSink.ACTION_CONNECTION_STATE_CHANGED);
        filter2.addAction(BluetoothHeadsetClient.ACTION_CONNECTION_STATE_CHANGED);
        if (getActivity() != null) {
            getActivity().registerReceiver(pairReceiver, filter1);
            getActivity().registerReceiver(connectReceiver, filter2);
        }
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        Log.i(TAG, "onDestroyView");
//        deviceAdapter.removeHandler();
//        bluetoothAdapter.cancelDiscovery();
//        if (getActivity() != null) {
//            getActivity().unregisterReceiver(pairReceiver);
//            getActivity().unregisterReceiver(connectReceiver);
//        }
//    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_device;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public DeviceViewModel initViewModel() {
        return new DeviceViewModel(BluetoothApplication.getInstance());
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.i(TAG, "onDestroy");
//        if (EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().unregister(this);
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if(switchImg.isSelected()){
//            deviceAdapter.notifyDataSetChanged();
//        }
//
//    }

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
//
//    private void updateList(BluetoothDevice device) {
//        if (deviceViewModel.getDeviceList() != null
//                && deviceViewModel.getDeviceList().getValue() != null) {
//            deviceList = deviceViewModel.getDeviceList().getValue();
//        }
//        int index = deviceList.indexOf(device);
//        if (index != -1) {
//            deviceList.remove(device);
//            deviceList.add(index, device);
//        }
//        if (deviceViewModel != null) {
//            deviceViewModel.setDeviceList(deviceList);
//        }
//        deviceAdapter.moveListToDevice(device);
//    }


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

    class BluetoothPairReceiver extends BroadcastReceiver {

        String TAG = "_BluetoothPairReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "接收到广播");
//            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
//                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                if (bondState == BluetoothDevice.BOND_BONDED) {
//                    Log.d(TAG, "配对完成, 移动列表至顶部");
//                    updateList(device);
//                } else if (bondState == BluetoothDevice.BOND_NONE) {
//                    Log.d(TAG, "取消配对");
//                    if (deviceViewModel.getDeviceList() != null
//                            && deviceViewModel.getDeviceList().getValue() != null) {
//                        deviceList = deviceViewModel.getDeviceList().getValue();
//                    }
//                    deviceList.remove(device);
//                    deviceList.add(device);
//                    if (deviceViewModel != null) {
//                        deviceViewModel.setDeviceList(deviceList);
//                    }
//                    deviceAdapter.moveListToDevice(device);
//                } else if (bondState == BluetoothDevice.BOND_BONDING) {
//                    updateList(device);
//                }
//            }
        }
    }

    class BluetoothConnectionReceiver_ extends BroadcastReceiver {

        private final static String TAG = "BluetoothConnectionReceiver_";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean isHfpAction = BluetoothHeadsetClient.ACTION_CONNECTION_STATE_CHANGED.equals(action);
            boolean isA2dpAction = BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED.equals(action);
            boolean isA2dpSinkAction = BluetoothA2dpSink.ACTION_CONNECTION_STATE_CHANGED.equals(action);
            if (isHfpAction || isA2dpAction || isA2dpSinkAction) {
                int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
//                    if (state == BluetoothProfile.STATE_DISCONNECTED) {
//                        updateList(device);
//                    } else if (state == BluetoothProfile.STATE_CONNECTING) {
//                        updateList(device);
//                    } else if (state == BluetoothProfile.STATE_CONNECTED) {
//                        updateList(device);
//                    }
                }
            }

        }
    }
}