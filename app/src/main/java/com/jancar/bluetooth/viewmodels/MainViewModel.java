package com.jancar.bluetooth.viewmodels;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

/**
 * @author suhy
 */
public class MainViewModel extends ViewModel {
    private MutableLiveData<Integer> selectedPage = new MutableLiveData<>();

    public MainViewModel() {
        selectedPage = new MutableLiveData<>();
        selectedPage.setValue(0);
    }
    public LiveData<Integer> getSelectedPage() {
        return selectedPage;
    }

    public void setSelectedPage(int position) {
        selectedPage.setValue(position);
    }
}
