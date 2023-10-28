package com.jancar.bluetooth.adapters;

import static com.jancar.bluetooth.utils.BluetoothUtil.getConnectStatus;
import static com.jancar.bluetooth.utils.BluetoothUtil.getPairingStatus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jancar.bluetooth.R;
import android.bluetooth.BluetoothDevice;

import java.util.Set;

/**
 * @author suhy
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private Set<BluetoothDevice> deviceSet;

    public DeviceAdapter(Set<BluetoothDevice> deviceSet) {
        this.deviceSet = deviceSet;
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
        holder.deviceName.setText(device.getName());
        holder.deviceAddress.setText(device.getAddress());
        holder.pairingStatus.setText(getPairingStatus(device.getBondState()));
//        holder.connectStatus.setText(getConnectStatus(device.getConnectStatus()));
        
        holder.itemView.setOnClickListener( v -> {
            
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

}
