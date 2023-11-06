package com.jancar.bluetooth.viewmodels;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

/**
 * @author suhy
 */
public class MusicViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MusicViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}