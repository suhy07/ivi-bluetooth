package com.jancar.bluetooth.adapters;

import static com.jancar.bluetooth.utils.BluetoothUtil.getConnectStatus;
import static com.jancar.bluetooth.utils.BluetoothUtil.getPairingStatus;

import android.annotation.NonNull;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;
import com.jancar.btservice.bluetooth.IBluetoothExecCallback;
import com.jancar.sdk.bluetooth.BluetoothManager;

import android.bluetooth.BluetoothDevice;

import java.util.HashSet;
import java.util.Set;

/**
 * @author suhy
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private final static String TAG = DeviceAdapter.class.getName();
    private Set<BluetoothDevice> deviceSet;
    private DeviceViewModel deviceViewModel;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager jancarBluetoothManager;

    public DeviceAdapter(Set<BluetoothDevice> deviceSet,DeviceViewModel deviceViewModel) {
        this.deviceSet = deviceSet;
        this.deviceViewModel = deviceViewModel;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        jancarBluetoothManager = MainApplication.getInstance().getBluetoothManager();
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
        holder.connectStatus.setText(getConnectStatus(device.isConnected()));

        holder.itemView.setOnClickListener( v -> {
            if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                jancarBluetoothManager.openBluetoothModule(stub);
                jancarBluetoothManager.unlinkDevice(stub);
                jancarBluetoothManager.linkDevice(deviceAddress,stub);
            } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                jancarBluetoothManager.stopContactOrHistoryLoad(stub);
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
        TextView pairButton = dialogView.findViewById(R.id.tv_pair);
        BluetoothDevice device = (BluetoothDevice) deviceSet.toArray()[position];
        if (device.isConnected()) {
            connectButton.setText(context.getString(R.string.disconnect));
        } else {
            connectButton.setText(context.getString(R.string.connect));
        }
        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
            pairButton.setText(context.getString(R.string.unpair));
        } else {
            pairButton.setText(context.getString(R.string.pair_status_pair));
        }
                // 处理关闭按钮点击事件
        closeButton.setOnClickListener(v -> dialog.dismiss());

        pairButton.setOnClickListener(v -> {
            if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                device.removeBond();
            } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                device.createBond();
            }
            Set<BluetoothDevice> devices = new HashSet<>(deviceViewModel.getDeviceSet().getValue());
            devices.remove(device);
            devices.add(device);
            deviceViewModel.setDeviceSet(devices);
            dialog.dismiss();
        });

        connectButton.setOnClickListener(v -> {
            if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                device.createBond();
            } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                jancarBluetoothManager.disconnect();
                if (!device.isConnected()) {
//                    new AcceptThread().start();
//                    new ConnectThread(device).start();
//                    jancarBluetoothManager.connect();
                }
            }
            Set<BluetoothDevice> devices = new HashSet<>(deviceViewModel.getDeviceSet().getValue());
            devices.remove(device);
            devices.add(device);
            deviceViewModel.setDeviceSet(devices);
            dialog.dismiss();
        });
        // 处理取消配对按钮点击事件

        dialog.show();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    IBluetoothExecCallback.Stub stub =  new IBluetoothExecCallback.Stub() {
        @Override
        public void onSuccess(String s) {
            Log.i(TAG, s);
        }

        @Override
        public void onFailure(int i) {
            Log.i(TAG, "onFailure" + i);
        }
    };
}
