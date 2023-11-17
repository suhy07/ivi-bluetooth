package com.jancar.bluetooth.viewmodels;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.jancar.bluetooth.model.CallLog;
import com.jancar.bluetooth.model.Contact;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author suhy
 */
public class AddressViewModel extends ViewModel {
    private MutableLiveData<List<Contact>> contactList;
    private MutableLiveData<List<CallLog>> callLogList;
    private MutableLiveData<Integer> selectedPage;

    public AddressViewModel() {
        this.contactList = new MutableLiveData<>();
        this.callLogList = new MutableLiveData<>();
        this.selectedPage = new MutableLiveData<>();
        contactList.setValue(new ArrayList<>());
        callLogList.setValue(new ArrayList<>());

    }

    public LiveData<List<Contact>> getContactList() {
        return contactList;
    }

    public void setContactList(List<Contact> contactList) {
        this.contactList.setValue(contactList);
    }

    public LiveData<List<CallLog>> getCallLogList() {
        return callLogList;
    }

    public void setCallLogList(List<CallLog> callLogList) {
        this.callLogList.setValue(callLogList);
    }

    public MutableLiveData<Integer> getSelectedPage() {
        return selectedPage;
    }

    public void setSelectedPage(int selectedPage) {
        this.selectedPage.setValue(selectedPage);
    }

}