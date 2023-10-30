package com.jancar.bluetooth.adapters;

import static com.jancar.bluetooth.utils.BluetoothUtil.getConnectStatus;
import static com.jancar.bluetooth.utils.BluetoothUtil.getPairingStatus;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jancar.bluetooth.R;
import com.jancar.bluetooth.service.ConnectThread;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;

import android.bluetooth.BluetoothDevice;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author suhy
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private Set<BluetoothDevice> deviceSet;
    private DeviceViewModel deviceViewModel;
    private BluetoothAdapter bluetoothAdapter;
    private Map<String, Boolean> connectStatusMap;

    public DeviceAdapter(Set<BluetoothDevice> deviceSet, Map<String, Boolean> connectStatusMap,DeviceViewModel deviceViewModel) {
        this.deviceSet = deviceSet;
        this.deviceViewModel = deviceViewModel;
        this.connectStatusMap = connectStatusMap;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    public void setDeviceSet(Set<BluetoothDevice> devices) {
        this.deviceSet = devices;
    }

    public void setConnStatus(Map<String, Boolean> connStatus) {
        this.connectStatusMap = connStatus;
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

        // 检查连接状态并设置相应的 UI
        boolean isConnected = connectStatusMap.getOrDefault(deviceAddress, false);
        holder.connectStatus.setText(getConnectStatus(isConnected));

        holder.itemView.setOnClickListener( v -> {
            if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                // 尝试连接到蓝牙设备
                new ConnectThread(device).start();
//                holder.connectStatus.setText(getConnectStatus(bluetoothSocket.isConnected()));
            } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                holder.pairingStatus.setText(getPairingStatus(BluetoothDevice.BOND_BONDING));
                device.createBond();
            } else if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                holder.pairingStatus.setText(getPairingStatus(device.getBondState()));
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
                Log.d("?!" , e.getMessage());
            }
            dialog.dismiss();
        });
        dialog.show();
    }

}
