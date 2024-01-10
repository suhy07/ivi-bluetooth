package com.jancar.bluetooth.adapters;

import static com.jancar.bluetooth.utils.BluetoothUtil.getConnectStatus;
import static com.jancar.bluetooth.utils.BluetoothUtil.getPairingStatus;

import android.annotation.NonNull;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.utils.CallUtil;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;
import com.jancar.btservice.bluetooth.IBluetoothExecCallback;
import com.jancar.sdk.bluetooth.BluetoothManager;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;

/**
 * @author suhy
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private final static String TAG = "DeviceAdapter";
    private List<BluetoothDevice> deviceList;
    private DeviceViewModel deviceViewModel;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager jancarBluetoothManager;
    private RecyclerView recyclerView;
    private BluetoothDevice nowDevice;
    private DeviceViewHolder holder;
    private final static int UPDATE_LIST = 0;
    private final DeviceAdapter.mHandler mHandler = new DeviceAdapter.mHandler();

    public DeviceAdapter(Set<BluetoothDevice> deviceSet
            , DeviceViewModel deviceViewModel, RecyclerView recyclerView) {
        sortDeviceList(deviceSet);
        this.deviceList = new ArrayList<>(deviceSet);
        this.deviceViewModel = deviceViewModel;
        this.recyclerView = recyclerView;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        jancarBluetoothManager = MainApplication.getInstance().getBluetoothManager();
    }

    public void setDeviceSet(Set<BluetoothDevice> devices) {
        this.deviceList = new ArrayList<>(devices);
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        this.holder = holder;
        ViewCompat.setBackground(holder.itemView, createSelectableBackground(holder.itemView.getContext()));
        BluetoothDevice device = (BluetoothDevice) deviceList.toArray()[position];
        String deviceName = device.getName();
        String deviceAddress = device.getAddress();
        int devicePairStatus = device.getBondState();
        if(deviceName == null || deviceName.equals("")) {
            deviceName = MainApplication.getInstance().getString(R.string.str_unknown_device);
        }
        holder.deviceName.setText(deviceName);
        holder.deviceAddress.setText(deviceAddress);
        holder.pairingStatus.setText(getPairingStatus(devicePairStatus));
        holder.connectStatus.setText(getConnectStatus(device));
        if(CallUtil.getInstance().isDeviceConnecting(device) || CallUtil.getInstance().isDeviceConnected(device) ||
                devicePairStatus == BluetoothDevice.BOND_BONDING) {
            holder.deviceName.setTextColor(0xFF00C2C2);
            holder.deviceAddress.setTextColor(0xFF00C2C2);
            holder.pairingStatus.setTextColor(0xFF00C2C2);
            holder.connectStatus.setTextColor(0xFF00C2C2);
        } else {
            holder.deviceName.setTextColor(0xFFFFFFFF);
            holder.deviceAddress.setTextColor(0xFFFFFFFF);
            holder.pairingStatus.setTextColor(0xFFFFFFFF);
            holder.connectStatus.setTextColor(0xFFFFFFFF);
        }
        if(device.isConnected()) {
            Global.connStatus = Global.CONNECTED;
        }
        holder.itemView.setOnClickListener( v -> {
            nowDevice = device;
            sortDeviceList(new HashSet<>(deviceList));
            if (CallUtil.getInstance().isDeviceConnected(device)) {
                //已连接，只断开
                jancarBluetoothManager.unlinkDevice(unlinkStub);
//                reFreshDeviceSet(device);
            } else {
                //未连接
                //已配对
                if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                    jancarBluetoothManager.unlinkDevice(unlinkStub);
                    startConnect(device);
                //未配对
                } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    resumeBluetooth();
                    startPair(device);
                } else if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    holder.pairingStatus.setText(getPairingStatus(device.getBondState()));
                }
//                recyclerView.smoothScrollToPosition(0);
            }
        });
        holder.itemView.setOnLongClickListener((view) -> {
            showOptionsDialog(device, view.getContext());
            holder.itemView.getParent().requestDisallowInterceptTouchEvent(true);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public void sortDeviceList(Set<BluetoothDevice> deviceSet) {
        final Set<BluetoothDevice> devices = new HashSet<>(deviceSet);
        new Thread(
                ()->{
                    List<BluetoothDevice> sortDeviceList, sortDeviceList1,
                    sortDeviceList2, sortDeviceList3;
                    sortDeviceList = new ArrayList<>();
                    sortDeviceList1 = new ArrayList<>();
                    sortDeviceList2 = new ArrayList<>();
                    sortDeviceList3 = new ArrayList<>();
                    List<BluetoothDevice> tempList;
                    tempList = new ArrayList<>(devices);

                    for (BluetoothDevice device : devices) {
                        if (device.isConnected()) {
                            sortDeviceList.add(device);
                            tempList.remove(device);
                        } else if (CallUtil.getInstance().isDeviceConnecting(device)) {
                            sortDeviceList1.add(device);
                            tempList.remove(device);
                        } else if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                            sortDeviceList2.add(device);
                            tempList.remove(device);
                        } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                            sortDeviceList3.add(device);
                            tempList.remove(device);
                        }
                    }
                    sortDeviceList.addAll(sortDeviceList1);
                    sortDeviceList.addAll(sortDeviceList2);
                    sortDeviceList.addAll(sortDeviceList3);
                    sortDeviceList.addAll(tempList);
                    mHandler.removeMessages(UPDATE_LIST);
                    Message msg = Message.obtain();
                    msg.what = UPDATE_LIST;
                    msg.obj = sortDeviceList;
                    mHandler.sendMessage(msg);
                }
        ).start();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView pairingStatus;
        TextView connectStatus;

        DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.tv_device_name);
            deviceAddress = itemView.findViewById(R.id.tv_device_address);
            pairingStatus = itemView.findViewById(R.id.tv_device_pairing_status);
            connectStatus = itemView.findViewById(R.id.tv_device_connect_status);
        }
    }


    // 创建点击效果
    private Drawable createSelectableBackground(Context context) {
        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        Drawable selectableBackground = typedArray.getDrawable(0);
        typedArray.recycle();
        return selectableBackground;
    }

    private void showOptionsDialog(BluetoothDevice device, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_options, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button connectButton = dialogView.findViewById(R.id.btn_connect);
        Button pairButton = dialogView.findViewById(R.id.btn_pair);
        int bondState = device.getBondState();
        boolean isConnected = device.isConnected();
        if (isConnected) {
            connectButton.setText(context.getString(R.string.disconnect));
        } else {
            connectButton.setText(context.getString(R.string.connect));
        }
        if (bondState == BluetoothDevice.BOND_BONDED) {
            pairButton.setText(context.getString(R.string.unpair));
        } else {
            pairButton.setText(context.getString(R.string.pair_status_pair));
        }

        //处理配对
        pairButton.setOnClickListener(v -> {
            if (bondState == BluetoothDevice.BOND_BONDED){
                device.removeBond();
            } else if (bondState == BluetoothDevice.BOND_NONE) {
                startPair(device);
            }
            dialog.dismiss();
        });
        //处理连接
        connectButton.setOnClickListener(v -> {
            if (bondState == BluetoothDevice.BOND_NONE) {
                resumeBluetooth();
                startPair(device);
            } else if (bondState == BluetoothDevice.BOND_BONDED) {
                jancarBluetoothManager.unlinkDevice(unlinkStub);
                if(!device.isConnected()) {
                    startConnect(device);
                }
            }
            dialog.dismiss();
        });
        dialog.show();
        dialog.getWindow().setLayout(300, 200);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    IBluetoothExecCallback.Stub unlinkStub = new IBluetoothExecCallback.Stub() {
        @Override
        public void onSuccess(String s) {

        }

        @Override
        public void onFailure(int i) {

        }
    };

    private void startConnect(BluetoothDevice device) {
        if(!CallUtil.getInstance().isConnecting()) {
            Global.connStatus = Global.CONNECTING;
            jancarBluetoothManager.linkDevice(device.getAddress(), stub);
        }
    }

    private void startPair(BluetoothDevice device) {
        if (!CallUtil.getInstance().isPairing(deviceList)) {
            device.createBond();
            holder.pairingStatus.setText(getPairingStatus(BluetoothDevice.BOND_BONDING));
        }
    }

    private void resumeBluetooth(){
//        bluetoothAdapter.cancelDiscovery();
//        jancarBluetoothManager.stopContactOrHistoryLoad(null);
    }

    class mHandler extends Handler {
        //重写handleMessage（）方法
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //执行的UI操作
            switch (msg.what) {
                case UPDATE_LIST:
                    List<BluetoothDevice> deviceSet = (List<BluetoothDevice>) msg.obj;
                    deviceList = new ArrayList<>(deviceSet);
                    if (nowDevice != null) {
                        int index;
                        try {
                           index = deviceList.indexOf(nowDevice);
                           recyclerView.smoothScrollToPosition(index);
                        } catch (Exception e) {
                            index = 0;
                            recyclerView.smoothScrollToPosition(index);
                        }
                    }
                    notifyDataSetChanged();
                    break;
            }
        }
    }
    IBluetoothExecCallback.Stub stub = new IBluetoothExecCallback.Stub() {
        @Override
        public void onSuccess(String s) {
            Log.i(TAG, "onSuccess:" + s);
        }

        @Override
        public void onFailure(int i) {
            Log.i(TAG,"onFailure");
            if (!CallUtil.getInstance().isConnected()) {
                Log.i(TAG, "tips");
                MainApplication.showToast(MainApplication.getInstance().getString(R.string.str_connect_on_failure_tips));
            }
        }
    };

    public void removeHandler() {
        mHandler.removeCallbacksAndMessages(this);
    }
}
