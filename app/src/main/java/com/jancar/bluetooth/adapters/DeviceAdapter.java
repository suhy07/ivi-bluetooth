package com.jancar.bluetooth.adapters;

import static com.jancar.bluetooth.utils.BluetoothUtil.connectToDevice;
import static com.jancar.bluetooth.utils.BluetoothUtil.getConnectStatus;
import static com.jancar.bluetooth.utils.BluetoothUtil.getPairingStatus;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jancar.bluetooth.R;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;

import android.bluetooth.BluetoothDevice;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author suhy
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private Set<BluetoothDevice> deviceSet;
    private DeviceViewModel deviceViewModel;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice targetDevice;
    private BluetoothSocket bluetoothSocket;

    public DeviceAdapter(Set<BluetoothDevice> deviceSet, DeviceViewModel deviceViewModel) {
        this.deviceSet = deviceSet;
        this.deviceViewModel = deviceViewModel;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void setDeviceSet(Set<BluetoothDevice> devices) {
        this.deviceSet = devices;
    }


    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        BluetoothDevice device = (BluetoothDevice) deviceSet.toArray()[position];
        String deviceName = device.getName();
        String deviceAddress = device.getAddress();
        int devicePairStatus = device.getBondState();
        holder.deviceName.setText(deviceName);
        holder.deviceAddress.setText(deviceAddress);
        holder.pairingStatus.setText(getPairingStatus(devicePairStatus));

        holder.itemView.setOnClickListener( v -> {
            Log.d("status", device.getBondState() + "");
            holder.pairingStatus.setText(getPairingStatus(device.getBondState()));
            if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                ConnectThread connectThread = new ConnectThread(device);
                connectThread.start();
                holder.connectStatus.setText(getConnectStatus(bluetoothSocket.isConnected()));
            } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                device.createBond();
                new Thread(()->{
                    try {
                        Thread.sleep(15000);
                        if(device.getBondState() == BluetoothDevice.BOND_BONDING){
                            holder.pairingStatus.setText(getPairingStatus(-1));
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            } else if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                return;
            }
        });
        holder.itemView.setOnLongClickListener((view) -> {
            showOptionsDialog(position, view.getContext());
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return deviceSet.size();
    }

    // 内部类，用于连接蓝牙设备
    private class ConnectThread extends Thread {
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(Global.MY_UUID);
            } catch (IOException e) {
                Log.e("TAG", "Socket's create() method failed", e);
            }
            bluetoothSocket = tmp;
        }

        public void run() {
            // 取消发现设备的操作，以免影响连接
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }

            try {
                // 连接到蓝牙设备
                bluetoothSocket.connect();
            } catch (IOException connectException) {
                try {
                    bluetoothSocket.close();
                } catch (IOException closeException) {
                    Log.e("TAG", "Could not close the client socket", closeException);
                }
                return;
            }

            // 连接成功，可以在 mmSocket 上进行数据传输
//            manageConnectedSocket(mmSocket);
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e("TAG", "Could not close the client socket", e);
            }
        }
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

        ImageView closeButton = dialogView.findViewById(R.id.btn_close);
        TextView connectButton = dialogView.findViewById(R.id.tv_connect);
        TextView unpairButton = dialogView.findViewById(R.id.tv_cancel);
        BluetoothDevice device = (BluetoothDevice) deviceSet.toArray()[position];
                // 处理关闭按钮点击事件
        closeButton.setOnClickListener(v -> dialog.dismiss());

        // 处理连接按钮点击事件
        connectButton.setOnClickListener(v -> {
            // 处理连接操作
            dialog.dismiss();
        });

        // 处理取消配对按钮点击事件
        unpairButton.setOnClickListener(v -> {
            // 处理取消配对操作
            try {
                Method m = device.getClass()
                        .getMethod("removeBond", (Class[]) null);
                m.invoke(device, (Object[]) null);
            } catch (Exception e) {
                Log.d(getClass().getName(), e.getMessage());
            }
            deviceSet.add(device);
            deviceViewModel.setDeviceSet(deviceSet);
            dialog.dismiss();
        });

        dialog.show();
    }

}
