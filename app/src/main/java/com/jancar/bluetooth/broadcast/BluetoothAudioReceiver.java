package com.jancar.bluetooth.broadcast;

import android.bluetooth.BluetoothA2dp;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothAudioReceiver extends BroadcastReceiver {
    private final static String TAG = "BluetoothAudioReceiver";
    private BluetoothA2dp a2dp;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG,"onReceive action="+action);
        //A2DP连接状态改变
        if(action.equals(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)){
            int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_DISCONNECTED);
            Log.i(TAG,"connect state="+state);
        }else if(action.equals(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED)){
            //A2DP播放状态改变
            int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_NOT_PLAYING);
            Log.i(TAG,"play state="+state);
        }
    }
}
