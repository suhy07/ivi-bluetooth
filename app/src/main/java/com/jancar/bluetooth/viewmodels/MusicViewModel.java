package com.jancar.bluetooth.viewmodels;


import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

/**
 * @author suhy
 */
public class MusicViewModel extends ViewModel {

    private final MutableLiveData<String> musicName;


    private final MutableLiveData<String> artist;

    public MusicViewModel() {
        musicName = new MutableLiveData<>();
        musicName.setValue("");
        artist = new MutableLiveData<>();
        artist.setValue("");
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

}