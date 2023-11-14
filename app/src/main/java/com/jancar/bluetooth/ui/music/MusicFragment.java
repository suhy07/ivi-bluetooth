package com.jancar.bluetooth.ui.music;

import android.annotation.NonNull;
import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.os.IBinder;
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
import com.jancar.sdk.car.IVICar;
import com.jancar.sdk.media.MediaManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

        }

        @Override
        public void onFailure(int i) {

        }
    };

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void setMusicViewModel(MusicViewModel musicViewModel) {
        this.musicViewModel = musicViewModel;
    }
}