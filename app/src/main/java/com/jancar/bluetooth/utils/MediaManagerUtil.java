package com.jancar.bluetooth.utils;

import android.content.Context;
import android.util.Log;

import com.jancar.sdk.BaseManager;
import com.jancar.sdk.media.IVIMedia;
import com.jancar.sdk.media.MediaManager;
import com.jancar.sdk.utils.Logcat;

import org.greenrobot.eventbus.EventBus;

public class MediaManagerUtil {
    private final static String TAG = "MediaManagerUtil";
    private MediaManager mMediaManager = null; // 获取媒体数据
    private Context mContext;
    public int mMediaType = IVIMedia.Type.NONE; // 音乐内部记忆的状态，不一定是当前系统的媒体状态

    public MediaManagerUtil(Context context, IVIMedia.MediaControlListener listener) {
        if (context != null) {
            mContext = context.getApplicationContext();
            mMediaManager = new MediaManager(mContext, mMediaConnectListener, listener, false); // MediaManager

        }

//        registerEventBus(this);
    }


    private BaseManager.ConnectListener mMediaConnectListener = new BaseManager.ConnectListener() {
        @Override
        public void onServiceConnected() {
            Logcat.d();
        }

        @Override
        public void onServiceDisconnected() {
            Logcat.d();
            if (mMediaManager != null) {
                mMediaManager.connect();
            }
        }
    };

    public static void registerEventBus(Object object) {
        if (!EventBus.getDefault().isRegistered(object)) {
            EventBus.getDefault().register(object);
        }
    }

    public boolean isMediaOpened(int mediaType) {
        return mMediaManager == null ? false : mMediaManager.isOpened(mediaType);
    }

    public void open(int type) {
        mMediaType = type;
        mMediaManager.open(type);
        mMediaManager.setCurrentShownMediaType(type);
        Log.d(TAG, "type:" + IVIMedia.Type.getName(type));
    }

    public void close(int type) {
        if (mMediaType != type) {
            mMediaType = IVIMedia.Type.NONE;
        }
        mMediaManager.close(type);
        Log.d(TAG,"type:" + IVIMedia.Type.getName(type));
    }

}
