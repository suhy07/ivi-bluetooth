package com.jancar.bluetooth.adapters;

import android.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.model.CallLog;

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
        if(name.equals("")) {
            name = MainApplication.getInstance().getString(R.string.str_unknown_call);
        }
        holder.callName.setText(name);
        holder.callNum.setText(callLog.getCallNumber());
        holder.callTime.setText(callLog.getCallTime());
    }

    @Override
    public int getItemCount() {
        return callLogs.size();
    }

    public static class CallLogViewHolder extends RecyclerView.ViewHolder {
        TextView callName;
        TextView callNum;
        TextView callTime;

        CallLogViewHolder(@NonNull View itemView) {
            super(itemView);
            callName = itemView.findViewById(R.id.tv_call_log_name);
            callNum = itemView.findViewById(R.id.tv_call_log_number);
            callTime = itemView.findViewById(R.id.tv_call_log_time);
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
