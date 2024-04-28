package com.jancar.bluetooth.ui.music;


import android.annotation.NonNull;
import android.app.Application;
import android.arch.lifecycle.MutableLiveData;

import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * @author suhy
 */
public class MusicViewModel extends BaseViewModel {

    private final MutableLiveData<String> musicName;
    private final MutableLiveData<String> artist;
    private final MutableLiveData<Integer> a2dpStatus;

    public MusicViewModel(@NonNull Application application) {
        super(application);
        musicName = new MutableLiveData<>();
        musicName.setValue("");
        artist = new MutableLiveData<>();
        artist.setValue("");
        a2dpStatus = new MutableLiveData<>();
        a2dpStatus.setValue(-1);
    }

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