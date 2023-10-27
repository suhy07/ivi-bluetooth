package com.jancar.bluetooth.viewmodels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author suhy
 */
public class MainViewModel extends ViewModel {
    private MutableLiveData<Integer> selectedPage = new MutableLiveData<>();

    public LiveData<Integer> getSelectedPage() {
        return selectedPage;
    }

    public void setSelectedPage(int position) {
        selectedPage.setValue(position);
    }
}
