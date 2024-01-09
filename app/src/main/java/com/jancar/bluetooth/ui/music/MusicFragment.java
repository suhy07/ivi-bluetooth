package com.jancar.bluetooth.ui.music;

import android.annotation.NonNull;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.utils.CallUtil;
import com.jancar.bluetooth.utils.MediaManagerUtil;
import com.jancar.bluetooth.viewmodels.MusicViewModel;
import com.jancar.btservice.bluetooth.IBluetoothExecCallback;
import com.jancar.sdk.BaseManager;
import com.jancar.sdk.bluetooth.BluetoothManager;
import com.jancar.sdk.bluetooth.IVIBluetooth;
import com.jancar.sdk.media.IVIMedia;
import com.jancar.sdk.media.MediaManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author suhy
 */
public class MusicFragment extends Fragment implements AudioManager.OnAudioFocusChangeListener {

    private final static String TAG = "MusicFragment" ;
    private TextView musicNameTv, artistTv;
    private ImageButton playBtn, prevBtn, nextBtn;
    private MusicViewModel musicViewModel;
    private BluetoothManager bluetoothManager;
    private AudioManager audioManager;
    //private AudioFocusRequest focusRequest;
    private AudioAttributes playbackAttributes;
    public MediaManager mMediaManager = null;

    private int status = -1;


    private View rootView;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_music, container, false);
        initView(rootView);
        init();

        mMediaManager = new MediaManager(getContext(), mMediaControlListener, mediaControlListener);

        if (musicViewModel != null) {
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
        }
        playBtn.setOnClickListener(v -> {
            if(CallUtil.isBluetoothSendStatus()){
                return;
            }
            if (!CallUtil.getInstance().canPlayBluetoothMusic()) {
                MainApplication.showToast(getString(R.string.str_not_connect_warn));
                return;
            }
            if(status != IVIBluetooth.BluetoothA2DPStatus.STREAMING){
                mMediaManager.open(IVIMedia.Type.A2DP);
            }

            bluetoothManager.playAndPause(iBluetoothExecCallback);

            updateMusicName();
        });
        prevBtn.setOnClickListener(v -> {
            if(CallUtil.isBluetoothSendStatus()){
                return;
            }
            if (!CallUtil.getInstance().canPlayBluetoothMusic()) {
                MainApplication.showToast(getString(R.string.str_not_connect_warn));
                return;
            }
            bluetoothManager.prevBtMusic(iBluetoothExecCallback);
            updateMusicName();
        });
        nextBtn.setOnClickListener(v -> {
            if(CallUtil.isBluetoothSendStatus()){
                return;
            }
            if (!CallUtil.getInstance().canPlayBluetoothMusic()) {
                MainApplication.showToast(getString(R.string.str_not_connect_warn));
                return;
            }
            bluetoothManager.nextBtMusic(stub);
            updateMusicName();
        });
        return rootView;
    }

    private void updateMusicName() {
        bluetoothManager.getBtMusicId3Info(iBluetoothExecCallback);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventA2DPId3Info(IVIBluetooth.EventMp3Id3Info event) {
        if (musicViewModel != null) {
            musicViewModel.setMusicName(event.name);
            musicViewModel.setArtist(event.artist);
        }
    }

    private long lastChangeTime = 0;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventModuleConnectStatus(IVIBluetooth.EventModuleConnectStatus event) {

        if(status == IVIBluetooth.BluetoothA2DPStatus.STREAMING && event.a2dpStatus == IVIBluetooth.BluetoothA2DPStatus.CONNECTED){
            lastChangeTime = System.currentTimeMillis();
        }
        status = event.a2dpStatus;

        if (musicViewModel != null) {
            musicViewModel.setA2dpStatus(event.a2dpStatus);
            Log.i(TAG, "event.a2dpStatus:"+event.a2dpStatus);
        }
    }

    private void initView(View rootView) {
        musicNameTv = rootView.findViewById(R.id.tv_music_name);
        artistTv = rootView.findViewById(R.id.tv_artist);
        playBtn = rootView.findViewById(R.id.btn_play);
        prevBtn = rootView.findViewById(R.id.btn_prev);
        nextBtn = rootView.findViewById(R.id.btn_next);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidthDp = Math.round(displayMetrics.widthPixels / displayMetrics.density);
        int screenHeightDp = Math.round(displayMetrics.heightPixels / displayMetrics.density);
        float density = displayMetrics.density;

        Log.d("ScreenInfo", "Screen Width (dp): " + screenWidthDp);
        Log.d("ScreenInfo", "Screen Height (dp): " + screenHeightDp);
        Log.d("ScreenInfo", "Screen Density: " + density);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

//        musicViewModel = new ViewModelProvider(this,
//                new ViewModelProvider.NewInstanceFactory()).get(MusicViewModel.class);
        bluetoothManager = MainApplication.getInstance().getBluetoothManager();

        audioManager = (AudioManager) requireContext().getSystemService(Context.AUDIO_SERVICE);

        playbackAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        /*focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(playbackAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(this, new Handler(Looper.getMainLooper()))
                .build();*/
    }


    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
//                  playbackNow();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                Log.i(TAG, "AUDIOFOCUS_LOSS");
                //bluetoothManager.pauseBtMusic(stub);
//                pausePlayback();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.i(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
//                pausePlayback();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.i(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                // ... 根据应用程序的需要进行暂停或降低音量
                break;
        }
    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (mMediaManager != null) {
            mMediaManager.close(IVIMedia.Type.A2DP);
        }
        bluetoothManager.pauseBtMusic(stub);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    private BaseManager.ConnectListener mMediaControlListener = new BaseManager.ConnectListener() {
        @Override
        public void onServiceConnected() {

        }

        @Override
        public void onServiceDisconnected() {

            if (mMediaManager != null) {
                mMediaManager.connect();
            }
        }
    };

    private boolean needResume = false;

    IVIMedia.MediaControlListener mediaControlListener = new IVIMedia.MediaControlListener() {
        @Override
        public void suspend() {
            long tempTime = System.currentTimeMillis();
            long diffTime = tempTime - lastChangeTime;
            Log.i(TAG, "suspend:"+status+" diffTime:"+diffTime);
            if(status == IVIBluetooth.BluetoothA2DPStatus.STREAMING || diffTime<350) {
                needResume = true;
            }
            bluetoothManager.pauseBtMusic(stub);
        }

        @Override
        public void stop() {
            Log.i(TAG, "stop");
            bluetoothManager.pauseBtMusic(stub);
        }

        @Override
        public void resume() {
            Log.i(TAG, "resume:"+needResume);
            if(needResume){
                needResume = false;
                bluetoothManager.playBtMusic(stub);
            }

        }
        @Override
        public void pause() {
            Log.i(TAG, "pause");
            bluetoothManager.pauseBtMusic(stub);
        }

        @Override
        public void play() {
            Log.i(TAG, "play");
            bluetoothManager.playBtMusic(stub);
        }

        @Override
        public void playPause() {
            Log.i(TAG, "playPause");
        }

        @Override
        public void setVolume(float volume) {

        }

        @Override
        public void next() {

        }

        @Override
        public void prev() {

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
        public void quitApp(int source) {

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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }
}