package com.jancar.bluetooth.ui.music;


import android.annotation.NonNull;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.jancar.bluetooth.BR;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.ui.main.MainItemViewModel;
import com.jancar.bluetooth.ui.main.MainViewModel;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

/**
 * @author suhy
 */
public class MusicViewModel extends MainItemViewModel {

    private final MutableLiveData<String> musicName;
    private final MutableLiveData<String> artist;
    private final MutableLiveData<Integer> a2dpStatus;

    public MusicViewModel(@NonNull MainViewModel mainViewModel) {
        super(mainViewModel);
        musicName = new MutableLiveData<>();
        musicName.setValue("");
        artist = new MutableLiveData<>();
        artist.setValue("");
        a2dpStatus = new MutableLiveData<>();
        a2dpStatus.setValue(-1);
    }

    public ItemBinding<MainItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.fragment_music);

    public void setMusicName(String musicName) {
        this.musicName.setValue(musicName);
    }

    public MutableLiveData<String> getMusicName() {
        return musicName;
    }

    public void setArtist(String artist) {
        this.artist.setValue(artist);
    }

    public MutableLiveData<String> getArtist() {
        return artist;
    }

    public MutableLiveData<Integer> getA2dpStatus() {
        return a2dpStatus;
    }

    public void setA2dpStatus(int a2dpStatus) {
        this.a2dpStatus.setValue(a2dpStatus);
    }
}