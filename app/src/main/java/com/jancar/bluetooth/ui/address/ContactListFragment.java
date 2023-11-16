package com.jancar.bluetooth.ui.address;

import static android.support.v4.content.ContextCompat.getSystemService;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;


import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.adapters.ContactAdapter;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.model.Contact;
import com.jancar.bluetooth.viewmodels.AddressViewModel;
import com.jancar.btservice.bluetooth.BluetoothDevice;
import com.jancar.btservice.bluetooth.BluetoothVCardBook;
import com.jancar.btservice.bluetooth.IBluetoothExecCallback;
import com.jancar.btservice.bluetooth.IBluetoothStatusCallback;
import com.jancar.btservice.bluetooth.IBluetoothVCardCallback;
import com.jancar.sdk.bluetooth.BluetoothManager;
import com.jancar.sdk.bluetooth.IVIBluetooth;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suhy
 */
public class ContactListFragment extends Fragment {

    private final static String TAG = "ContactListFragment";
    private ContactAdapter contactListAdapter;
    private ImageButton refreshBtn;
    private EditText searchEt;
    private RecyclerView recyclerView;
    private ProgressBar contactPb;
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
            Global.setContactList(contacts);
            contactListAdapter.setContactList(contacts);
            contactPb.setVisibility(View.GONE);
            contactListAdapter.notifyDataSetChanged();
        });
        refreshBtn.setOnClickListener(v -> {
           searchContact();
        });
        searchEt.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                hideKeyboard(v);
            }
        });
        rootView.setOnClickListener(v -> {
            hideKeyboard(v);
        });
        return rootView;
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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
        contactPb = rootView.findViewById(R.id.pb_contact);
        searchEt = rootView.findViewById(R.id.et_search);
    }

    public IBluetoothVCardCallback.Stub stub = new IBluetoothVCardCallback.Stub() {
        @Override
        public void onProgress(List<BluetoothVCardBook> list) {
            List<Contact> contacts = new ArrayList<>();
            for (BluetoothVCardBook vCardBook: list) {
                Contact contact = new Contact(vCardBook.name, vCardBook.phoneNumber);
                contacts.add(contact);
            }
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

    private IBluetoothExecCallback.Stub stub1 = new IBluetoothExecCallback.Stub() {
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

    private void searchContact() {
        if(Global.connStatus != Global.CONNECTED) {
            MainApplication.showToast(getString(R.string.str_not_connect_warn));
            return;
        }
        bluetoothManager.stopContactOrHistoryLoad(stub1);
        bluetoothManager.getPhoneContacts(stub);
        contactPb.setVisibility(View.VISIBLE);
        //超时
        new Thread(()-> {
            try {
                Thread.sleep(Global.TIMEOUT);
            } catch (InterruptedException e) {
                Log.i(TAG, e.getMessage());
            }
            getActivity().runOnUiThread(()->{
                contactPb.setVisibility(View.GONE);
            });
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        contactPb.setVisibility(View.INVISIBLE);
        if (addressViewModel.getContactList().getValue().isEmpty()) {
            searchContact();
        }
    }

}
