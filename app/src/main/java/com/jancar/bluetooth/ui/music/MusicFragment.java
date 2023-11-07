package com.jancar.bluetooth.ui.music;

import android.annotation.NonNull;
import android.arch.lifecycle.ViewModelProvider;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.service.media.MediaBrowserService;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.viewmodels.MusicViewModel;
import com.jancar.btservice.bluetooth.IBluetoothExecCallback;
import com.jancar.sdk.bluetooth.BluetoothManager;
import com.jancar.sdk.bluetooth.IVIBluetooth;

import android.media.session.MediaController;
import android.media.session.MediaSessionManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_music, container, false);
        if (isFirst) {
            initView(rootView);
            init();
            musicViewModel.getMusicName().observe(this, s -> {
                musicNameTv.setText(s);
            });
            musicViewModel.getArtist().observe(this, s -> {
                artistTv.setText(s);
            });
            playBtn.setOnClickListener(v -> {
                bluetoothManager.playAndPause(iBluetoothExecCallback);
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
            isFirst = false;
        }
        return rootView;
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

    private void initView(View rootView) {
        musicNameTv = rootView.findViewById(R.id.tv_music_name);
        artistTv = rootView.findViewById(R.id.tv_artist);
        playBtn = rootView.findViewById(R.id.btn_play);
        prevBtn = rootView.findViewById(R.id.btn_prev);
        nextBtn = rootView.findViewById(R.id.btn_next);
    }

    private void init() {
        EventBus.getDefault().register(this);
        musicViewModel = new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory()).get(MusicViewModel.class);
        bluetoothManager = MainApplication.getInstance().getBluetoothManager();
    }



    private IBluetoothExecCallback iBluetoothExecCallback = new IBluetoothExecCallback() {
        @Override
        public void onSuccess(String s) {
            Log.i(TAG, s);
            musicViewModel.setMusicName(s);
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

        }

        @Override
        public void onFailure(int i) {

        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}