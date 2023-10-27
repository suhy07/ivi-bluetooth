package com.jancar.bluetooth.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author suhy
 */
public class PhoneViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PhoneViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}