package com.jancar.bluetooth.ui.music;

import android.annotation.NonNull;
import android.arch.lifecycle.ViewModelProvider;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.utils.MediaManagerUtil;
import com.jancar.bluetooth.viewmodels.MusicViewModel;
import com.jancar.btservice.bluetooth.IBluetoothExecCallback;
import com.jancar.sdk.bluetooth.BluetoothManager;
import com.jancar.sdk.bluetooth.IVIBluetooth;
import com.jancar.sdk.car.IVICar;
import com.jancar.sdk.media.IVIMedia;
import com.jancar.sdk.media.MediaManager;
import com.jancar.sdk.utils.Logcat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * @author suhy
 */
public class MusicFragment extends Fragment {

    private final static String TAG = "MusicFragment" ;
    private static boolean isFirst = true;
    private TextView musicNameTv, artistTv;
    private ImageButton playBtn, prevBtn, nextBtn;
    private MusicViewModel musicViewModel;
    private BluetoothManager bluetoothManager;
    private MediaManagerUtil mediaManagerUtil;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_music, container, false);
        initView(rootView);
        init();
//        bluetoothManager.connect();
//        bluetoothManager.openBluetoothModule(stub);
        musicViewModel.getMusicName().observe(this, s -> {
            musicNameTv.setText(s);
        });
        musicViewModel.getArtist().observe(this, s -> {
            artistTv.setText(s);
        });
        musicViewModel.getA2dpStatus().observe(this, integer -> {
            if(integer == IVIBluetooth.BluetoothA2DPStatus.STREAMING) {
                playBtn.setBackground(getResources().getDrawable(R.drawable.ic_pause));
            } else {
                playBtn.setBackground(getResources().getDrawable(R.drawable.ic_play));
            }
        });
        playBtn.setOnClickListener(v -> {
            Log.i(TAG,"click Play");
            bluetoothManager.playAndPause(iBluetoothExecCallback);
            mediaManagerUtil.open(mediaManagerUtil.mMediaType);
            updateMusicName();
        });
        prevBtn.setOnClickListener(v -> {
            bluetoothManager.prevBtMusic(iBluetoothExecCallback);
            updateMusicName();
        });
        nextBtn.setOnClickListener(v -> {
            bluetoothManager.nextBtMusic(stub);
            updateMusicName();
        });
        return rootView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void updateMusicName() {
        bluetoothManager.getBtMusicId3Info(iBluetoothExecCallback);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventA2DPId3Info(IVIBluetooth.EventMp3Id3Info event) {
        musicViewModel.setMusicName(event.name);
        musicViewModel.setArtist(event.artist);
        Log.i(TAG, event.toString());
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventModuleConnectStatus(IVIBluetooth.EventModuleConnectStatus event) {
        musicViewModel.setA2dpStatus(event.a2dpStatus);
        Log.i(TAG, event.toString());
        Log.i(TAG, event.isStopped + "");
    }

    private void initView(View rootView) {
        musicNameTv = rootView.findViewById(R.id.tv_music_name);
        artistTv = rootView.findViewById(R.id.tv_artist);
        playBtn = rootView.findViewById(R.id.btn_play);
        prevBtn = rootView.findViewById(R.id.btn_prev);
        nextBtn = rootView.findViewById(R.id.btn_next);
    }

    private void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

//        musicViewModel = new ViewModelProvider(this,
//                new ViewModelProvider.NewInstanceFactory()).get(MusicViewModel.class);
        bluetoothManager = MainApplication.getInstance().getBluetoothManager();
        mediaManagerUtil = new MediaManagerUtil(getContext(), mMediaControlListener);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mediaManagerUtil.close(mediaManagerUtil.mMediaType);
    }

    IVIMedia.MediaControlListener mMediaControlListener = new IVIMedia.MediaControlListener() {
        @Override
        public void suspend() {
//            Logcat.d("isPlaying:" + isPlaying());
//            if (isPlaying()) {
//                if (isA2DPOpened()) {
//                    mA2DPService.suspend();
//                }
//                mIsNeedResume = true;
//            }
        }

        @Override
        public void stop() {
//            A2dpPresenter.this.stop();
        }

        @Override
        public void resume() {
//            Logcat.d("mIsResume:" + mIsNeedResume + ", " + mMediaManagerUtil.isActiveMedia(mMediaManagerUtil.getMediaType()));
//            Logcat.d("canPlayMedia:" + mMediaManagerUtil.canPlayMedia());
//            if (mMediaManagerUtil.canPlayMedia() && mMediaManagerUtil.isActiveMedia(mMediaManagerUtil.getMediaType())) {
//                if (isA2DPOpened()) { // 修改蓝牙音乐状态语音唤醒蓝牙电话，挂断电话之后 蓝牙音乐不恢复播放问题
//                    if (mIsNeedResume) {
//                        mIsNeedResume = false;
//                        mA2DPService.resume();
//                    }
//                } else if (mIsNeedResume) {
//                    mIsNeedResume = false;
//                }
//            } else {
//                Logcat.d("connot resume, return!!!");
//            }
        }

        @Override
        public void pause() {
//            A2dpPresenter.this.pause();
//            mIsNeedResume = false;
        }

        @Override
        public void play() {
//            start();
        }

        @Override
        public void playPause() {
//            if (mMediaManagerUtil.canPlayMedia() && isA2DPOpened()) {
//                playAndPause();
//            }
        }

        @Override
        public void setVolume(float volume) {
//            if (isA2DPOpened() && null != mA2DPService) {
//                mA2DPService.setVolume(volume);
//            }
        }

        @Override
        public void next() {
//            if (mMediaManagerUtil.canPlayMedia()) {
//                if (isA2DPOpened() && null != mA2DPService) {
//                    mA2DPService.next();
//                }
//            }
        }

        @Override
        public void prev() {
//            if (mMediaManagerUtil.canPlayMedia()) {
//                if (isA2DPOpened() && null != mA2DPService) {
//                    mA2DPService.prev();
//                }
//            }
        }

        @Override
        public void select(int index) {

        }

        @Override
        public void setFavour(boolean isFavour) {

        }

        @Override
        public void filter(String title, String singer) {

        }

        @Override
        public void playRandom() {

        }

        @Override
        public void setPlayMode(int mode) {

        }

        @Override
        public void quitApp(int source) { // 是否应该交由UI去处理 (音乐和蓝牙音乐，划掉的可能是非正在播放的type)
//            Logcat.d("source:" + source /*+ ", mediaType:" + mediaType*/);
//            if (source == IVIMedia.QuitMediaSource.VOICE) {
//                AppManager.getAppManager().finishAllActivity();
//            }
//
//            stop();
//            A2dpPresenter.this.pause();
        }

        @Override
        public void onVideoPermitChanged(boolean show) {

        }

        @Override
        public void seekTo(int msec) {

        }

        @Override
        public void setFrequencyDoubling(int operation, int rate) {

        }
    };

    public void setMusicViewModel(MusicViewModel musicViewModel) {
        this.musicViewModel = musicViewModel;
    }

    private IBluetoothExecCallback iBluetoothExecCallback = new IBluetoothExecCallback() {
        @Override
        public void onSuccess(String s) {
            Log.i(TAG, s);
        }

        @Override
        public void onFailure(int i) {
            Log.i(TAG, "onFailure" + i);
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    };

    private IBluetoothExecCallback.Stub stub = new IBluetoothExecCallback.Stub() {
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