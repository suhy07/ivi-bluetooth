package com.jancar.bluetooth.ui.device;

import android.annotation.NonNull;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.databinding.Observable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.databinding.adapters.TextViewBindingAdapter;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.jancar.bluetooth.R;
import com.jancar.bluetooth.global.Global;
import com.jancar.sdk.utils.Logcat;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.Messenger;
import me.tatarka.bindingcollectionadapter2.BR;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

/**
 * @author suhy
 */
public class DeviceViewModel extends BaseViewModel {

    private final String TAG = "DeviceViewModel";
    private final int STOP_SCAN = 0;

    public ObservableBoolean onOff = new ObservableBoolean();
    public ObservableInt scanVis = new ObservableInt(View.INVISIBLE);
    public ObservableField<String> renameBtnStr = new ObservableField<>(getApplication().getString(
            R.string.bluetooth_rename));
    public ObservableBoolean renameTvEnable = new ObservableBoolean(false);
    public ObservableList<DeviceItemViewModel> deviceList = new ObservableArrayList<DeviceItemViewModel>(){
        @Override
        public boolean contains(@Nullable Object o) {
            for (DeviceItemViewModel itemViewModel: this) {
                BluetoothDevice device = itemViewModel.device.get();
                if (o != null && device != null) {
                    if (device.equals(((DeviceItemViewModel)o).device.get())) {
                        return true;
                    }
                }
            }
            return  false;
        }
    };
    public ObservableField<String> bluetoothName = new ObservableField<>("");
    public ItemBinding<DeviceItemViewModel> itemBinding = ItemBinding.of(BR.viewModel,
            R.layout.device_item);

    private final BluetoothManager bluetoothManager;
    private final BluetoothAdapter bluetoothAdapter;
    private final mHandler mHandler = new mHandler();
    private final int scanTime = 10000;

    public DeviceViewModel(@NonNull Application application) {
        super(application);
        bluetoothManager = getApplication().getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
        onOff.set(bluetoothAdapter.isEnabled());
        bluetoothName.set(bluetoothAdapter.getName());
        init();

        Messenger.getDefault().register(this, Global.Tokens.TOKEN_DEVICEVIEWMODEL_ADDDEVICE, BluetoothDevice.class, device -> {
            DeviceItemViewModel item = new DeviceItemViewModel(this, device);
            if (!deviceList.contains(item)) {
                deviceList.add(item);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        deInit();
    }


    public BindingCommand pressOnOff = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if(bluetoothAdapter.isEnabled()){
                bluetoothAdapter.disable();
                onOff.set(false);
            }else{
                bluetoothAdapter.enable();
                onOff.set(true);
            }
        }
    });

    public BindingCommand pressScan = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (bluetoothAdapter.isDiscovering()) {
                return;
            }
            bluetoothAdapter.startDiscovery();
            scanVis.set(View.VISIBLE);
            mHandler.sendEmptyMessageDelayed(STOP_SCAN, scanTime);
        }
    });

    public BindingCommand pressRename = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (renameTvEnable.get()) {
                renameBtnStr.set(getApplication().getString(R.string.bluetooth_rename));
                renameTvEnable.set(false);
            } else {
                renameBtnStr.set(getApplication().getString(R.string.str_finish));
                renameTvEnable.set(true);
                InputMethodManager imm = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    });

    public TextViewBindingAdapter.AfterTextChanged afterTextChanged = editable -> {
        int textLength = editable.length();
        int maxLength = 21;
        if (textLength > maxLength) {
            // 如果超过限制，截取前面的限制字符
            editable.delete(maxLength, textLength);
        }
    };

    private void init() {
        for(BluetoothDevice device: bluetoothAdapter.getBondedDevices()) {
            DeviceItemViewModel item = new DeviceItemViewModel(this, device);
            deviceList.add(item);
            Logcat.d(TAG, device.getName());
        }
        bluetoothName.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                ObservableField<String> strSender = (ObservableField<String>) sender;
                bluetoothAdapter.setName(strSender.get());
            }
        });
    }

    private void deInit() {
        mHandler.removeCallbacksAndMessages(null);
    }

    class mHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STOP_SCAN:
                    scanVis.set(View.GONE);
                    if (bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}