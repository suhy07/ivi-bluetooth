package com.jancar.bluetooth.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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

    public AddressViewModel() {
        this.contactList = new MutableLiveData<>();
        this.callLogList = new MutableLiveData<>();
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
}