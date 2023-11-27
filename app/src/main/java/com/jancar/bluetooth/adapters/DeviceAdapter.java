package com.jancar.bluetooth.adapters;

import static com.jancar.bluetooth.utils.BluetoothUtil.getConnectStatus;
import static com.jancar.bluetooth.utils.BluetoothUtil.getPairingStatus;

import android.annotation.NonNull;
import android.annotation.PluralsRes;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.ui.device.DeviceFragment;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;
import com.jancar.btservice.bluetooth.IBluetoothExecCallback;
import com.jancar.sdk.bluetooth.BluetoothManager;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author suhy
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private final static String TAG = "DeviceAdapter";
    private List<BluetoothDevice> deviceList;
    private Map<BluetoothDevice, Integer> connMap;
    private DeviceViewModel deviceViewModel;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager jancarBluetoothManager;
    private final static int CONNECT_WHAT = 0;
    private final static int CONNECT_TIMEOUT = 1500;
    private final DeviceAdapter.mHandler mHandler = new DeviceAdapter.mHandler();

    public DeviceAdapter(Set<BluetoothDevice> deviceSet, Map<BluetoothDevice, Integer> connMap
            , DeviceViewModel deviceViewModel) {
        this.deviceList = sortDeviceList(deviceSet);
        this.deviceViewModel = deviceViewModel;
        this.connMap = connMap;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        jancarBluetoothManager = MainApplication.getInstance().getBluetoothManager();
    }

    public void setDeviceSet(Set<BluetoothDevice> devices) {
        this.deviceList = sortDeviceList(devices);
    }

    public void setConnMap(Map<BluetoothDevice, Integer> connMap) {
        this.connMap = connMap;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        BluetoothDevice device = (BluetoothDevice) deviceList.toArray()[position];
        String deviceName = device.getName();
        String deviceAddress = device.getAddress();
        int status = connMap.get(device);
        int devicePairStatus = device.getBondState();
        if(deviceName == null || deviceName.equals("")) {
            deviceName = MainApplication.getInstance().getString(R.string.str_unknown_device);
        }
        holder.deviceName.setText(deviceName);
        holder.deviceAddress.setText(deviceAddress);
        holder.pairingStatus.setText(getPairingStatus(devicePairStatus));
        holder.connectStatus.setText(getConnectStatus(status));
        if(status == Global.CONNECTING || status == Global.CONNECTED ) {
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
            if (device.isConnected()) {
                //已连接，只断开
                jancarBluetoothManager.unlinkDevice(unlinkStub);
                reFreshDeviceSet(device);
            } else {
                //未连接
                //已配对
                if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                    jancarBluetoothManager.unlinkDevice(unlinkStub);
                    startConnect(device);
                //未配对
                } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    resumeBluetooth();
                    holder.pairingStatus.setText(getPairingStatus(BluetoothDevice.BOND_BONDING));
                    device.createBond();
                } else if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    holder.pairingStatus.setText(getPairingStatus(device.getBondState()));
                }
            }
        });
        holder.itemView.setOnLongClickListener((view) -> {
            showOptionsDialog(position, view.getContext());
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    private List<BluetoothDevice> sortDeviceList(Set<BluetoothDevice> deviceSet) {
        List<BluetoothDevice> sortDeviceList = new ArrayList<>();
        List<BluetoothDevice> tempList;
        if (deviceSet != null) {
            tempList = new ArrayList<>(deviceSet);
        } else {
            tempList = new ArrayList<>();
        }
        for (BluetoothDevice device : deviceSet) {
            if (device.isConnected()) {
                sortDeviceList.add(device);
                tempList.remove(device);
            }
        }
        deviceSet = new HashSet<>(tempList);
        for (BluetoothDevice device : deviceSet) {
            if (device.getBondState() == BluetoothDevice.BOND_BONDED
            || device.getBondState() == BluetoothDevice.BOND_BONDING) {
                sortDeviceList.add(device);
                tempList.remove(device);
            }
        }
        sortDeviceList.addAll(tempList);
        return sortDeviceList;
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

    private void showOptionsDialog(int position, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_options, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button connectButton = dialogView.findViewById(R.id.btn_connect);
        Button pairButton = dialogView.findViewById(R.id.btn_pair);
        BluetoothDevice device = (BluetoothDevice) deviceList.toArray()[position];
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
                device.createBond();
            }
            dialog.dismiss();
        });
        //处理连接
        connectButton.setOnClickListener(v -> {
            if (bondState == BluetoothDevice.BOND_NONE) {
                resumeBluetooth();
                device.createBond();
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

    private void reFreshDeviceSet(BluetoothDevice device) {
        if (deviceViewModel != null) {
            Set<BluetoothDevice> devices = new HashSet<>(deviceViewModel.getDeviceSet().getValue());
            devices.remove(device);
            devices.add(device);
            deviceViewModel.setDeviceSet(devices);
        }
    }

    private void startConnect(BluetoothDevice device) {
        if(Global.connStatus != Global.CONNECTING) {
            Global.connStatus = Global.CONNECTING;
            connMap.remove(device);
            connMap.put(device, Global.CONNECTING);
            if (deviceViewModel != null) {
                deviceViewModel.setConnMap(connMap);
            }
            new Thread(() -> {
                synchronized (this) {
                    Message msg = Message.obtain();
                    msg.what = CONNECT_WHAT;
                    msg.obj = device;
                    mHandler.sendMessageDelayed(msg, CONNECT_TIMEOUT);
                }
            }).start();
        }
    }

    private void resumeBluetooth(){
        bluetoothAdapter.cancelDiscovery();
        jancarBluetoothManager.stopContactOrHistoryLoad(null);
    }

    class mHandler extends Handler {
        //重写handleMessage（）方法
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //执行的UI操作
            switch (msg.what) {
                case CONNECT_WHAT:
                    BluetoothDevice device = (BluetoothDevice) msg.obj;
                    jancarBluetoothManager.linkDevice(device.getAddress(), new IBluetoothExecCallback.Stub() {
                        @Override
                        public void onSuccess(String s) {
                            Global.connStatus = Global.CONNECTED;
                            reFreshDeviceSet(device);
                        }

                        @Override
                        public void onFailure(int i) {
                            Global.connStatus = Global.NOT_CONNECTED;
                            reFreshDeviceSet(device);
                        }
                    });
                    break;
            }
        }
    }
}
