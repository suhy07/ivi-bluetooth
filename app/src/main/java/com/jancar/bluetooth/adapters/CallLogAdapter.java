package com.jancar.bluetooth.adapters;

import android.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.model.CallLog;
import com.jancar.bluetooth.utils.CallUtil;
import com.jancar.btservice.bluetooth.BluetoothVCardBook;
import com.jancar.sdk.bluetooth.IVIBluetooth;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @author suhy
 */
public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder> {

    private List<CallLog> callLogs;

    public CallLogAdapter(List<CallLog> callLogs) {
        this.callLogs = callLogs;
    }

    @NonNull
    @Override
    public CallLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_log_item, parent, false);
        return new CallLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallLogViewHolder holder, int position) {
        CallLog callLog = callLogs.get(position);
        String name = callLog.getCallName();
        String type = callLog.getCallType();
        if(name.equals("")) {
            name = MainApplication.getInstance().getString(R.string.str_unknown_call);
        }
        if(type.equals(BluetoothVCardBook.DIAL_TYPE)){
            holder.callTypeIv.setImageDrawable(MainApplication.getInstance().getDrawable(R.drawable.ic_call_log_out));
        } else if (type.equals(BluetoothVCardBook.MISS_TYPE)){
            holder.callTypeIv.setImageDrawable(MainApplication.getInstance().getDrawable(R.drawable.ic_call_log_miss));
        } else {
            holder.callTypeIv.setImageDrawable(MainApplication.getInstance().getDrawable(R.drawable.ic_call_log_in));
        }
        if (type.equals(BluetoothVCardBook.MISS_TYPE)) {
            holder.callName.setTextColor(0xFFEB5546);
            holder.callNum.setTextColor(0xFFEB5546);
            holder.callTime.setTextColor(0xFFEB5546);
        } else {
            holder.callName.setTextColor(0xFFFFFFFF);
            holder.callNum.setTextColor(0xFFFFFFFF);
            holder.callTime.setTextColor(0xFFFFFFFF);
        }
        holder.callName.setText(name);
        holder.callNum.setText(callLog.getCallNumber());
        holder.callTime.setText(callLog.getCallTime());
        holder.itemView.setOnClickListener(v -> {
            String number = callLog.getCallNumber();
            CallUtil.getInstance().callPhone(number);
            EventBus.getDefault().post(new IVIBluetooth.CallStatus(IVIBluetooth.CallStatus.OUTGOING, number, false));
        });
    }

    @Override
    public int getItemCount() {
        return callLogs.size();
    }

    public static class CallLogViewHolder extends RecyclerView.ViewHolder {
        TextView callName;
        TextView callNum;
        TextView callTime;
        ImageView callTypeIv;

        CallLogViewHolder(@NonNull View itemView) {
            super(itemView);
            callName = itemView.findViewById(R.id.tv_call_log_name);
            callNum = itemView.findViewById(R.id.tv_call_log_number);
            callTime = itemView.findViewById(R.id.tv_call_log_time);
            callTypeIv = itemView.findViewById(R.id.iv_call_log);
        }
    }

    public void setCallLogs(List<CallLog> callLogs) {
        this.callLogs = callLogs;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
