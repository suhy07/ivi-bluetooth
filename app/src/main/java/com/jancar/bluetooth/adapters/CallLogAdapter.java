package com.jancar.bluetooth.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.callName.setText(callLog.getCallName());
        holder.callTime.setText(callLog.getCallTime());
    }

    @Override
    public int getItemCount() {
        return callLogs.size();
    }

    static class CallLogViewHolder extends RecyclerView.ViewHolder {
        TextView callName;
        TextView callTime;

        CallLogViewHolder(@NonNull View itemView) {
            super(itemView);
            callName = itemView.findViewById(R.id.tv_call_name);
            callTime = itemView.findViewById(R.id.tv_call_time);
        }
    }
}
