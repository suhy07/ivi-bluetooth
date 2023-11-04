package com.jancar.bluetooth.ui.address;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.adapters.ContactAdapter;
import com.jancar.bluetooth.model.Contact;
import com.jancar.bluetooth.viewmodels.AddressViewModel;
import com.jancar.btservice.bluetooth.BluetoothVCardBook;
import com.jancar.btservice.bluetooth.IBluetoothExecCallback;
import com.jancar.btservice.bluetooth.IBluetoothVCardCallback;
import com.jancar.sdk.bluetooth.BluetoothManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suhy
 */
public class ContactListFragment extends Fragment {

    private final String TAG = ContactListFragment.class.getName();
    private ContactAdapter contactListAdapter;
    private Button refreshBtn;
    private RecyclerView recyclerView;
    private List<Contact> contactList = new ArrayList<>();
    private AddressViewModel addressViewModel;
    private BluetoothManager bluetoothManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        initView(rootView);
        init();
        addressViewModel.getContactList().observe(getViewLifecycleOwner(), contacts -> {
            Log.d(TAG, "观察到contact变化");
            contactListAdapter.setContactList(contacts);
            contactListAdapter.notifyDataSetChanged();
        });
        refreshBtn.setOnClickListener(v -> {
            Log.i(TAG, "onclick");
            bluetoothManager.openBluetoothModule(new IBluetoothExecCallback.Stub() {
                @Override
                public void onSuccess(String s) throws RemoteException {

                }

                @Override
                public void onFailure(int i) throws RemoteException {

                }
            });
            bluetoothManager.getPhoneContacts(stub);
        });
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    private void init() {
        contactListAdapter = new ContactAdapter(contactList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(contactListAdapter);
        bluetoothManager = MainApplication.getInstance().getBluetoothManager();
    }

    private void initView(View rootView) {
        recyclerView = rootView.findViewById(R.id.rv_contact);
        refreshBtn = rootView.findViewById(R.id.btn_refresh_contact);
    }

    private IBluetoothVCardCallback.Stub stub =  new IBluetoothVCardCallback.Stub() {
        @Override
        public void onProgress(List<BluetoothVCardBook> list) {
            List<Contact> contacts = new ArrayList<>();
            for (BluetoothVCardBook vCardBook: list) {
                Contact contact = new Contact(vCardBook.name, vCardBook.phoneNumber);
                contacts.add(contact);
            }
            Log.i(TAG, list.toString());
            addressViewModel.setContactList(contacts);
        }

        @Override
        public void onFailure(int i) {
            Log.d(TAG, i + "");
        }

        @Override
        public void onSuccess(String s) {
            Log.d(TAG, s + "");
        }
    };

    public void setAddressViewModel(AddressViewModel addressViewModel) {
        this.addressViewModel = addressViewModel;
    }

}
