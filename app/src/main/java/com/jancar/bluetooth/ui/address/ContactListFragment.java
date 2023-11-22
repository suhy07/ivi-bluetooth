package com.jancar.bluetooth.ui.address;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.adapters.ContactAdapter;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.model.CallLog;
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

    private final static String TAG = "ContactListFragment";
    private ContactAdapter contactListAdapter;
    private ImageButton refreshBtn;
    private EditText searchEt;
    private RecyclerView recyclerView;
    private ProgressBar contactPb;
    private AddressViewModel addressViewModel;
    private BluetoothManager bluetoothManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        initView(rootView);
        init();
        if (addressViewModel != null) {
            addressViewModel.getContactList().observe(getViewLifecycleOwner(), contacts -> {
                Log.d(TAG, "观察到contact变化");
                contactListAdapter.setContactList(contacts);
                contactPb.setVisibility(View.GONE);
                contactListAdapter.notifyDataSetChanged();
            });
        }
        refreshBtn.setOnClickListener(v -> {
            if (Global.connStatus == Global.NOT_CONNECTED) {
                MainApplication.showToast(getString(R.string.str_not_connect_warn));
            } else {
                searchContact();
            }

        });
        searchEt.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                hideKeyboard(v);
            }
        });
        rootView.setOnClickListener(v -> {
            hideKeyboard(v);
        });
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String filter = s.toString();
                List<Contact> contacts = new ArrayList<>();
                List<Contact> beforeFilter = Global.getContactList();
                if(filter.equals("")) {
                    contacts = Global.getContactList();
                } else {
                    if(beforeFilter != null) {
                        for (Contact contact: beforeFilter) {
                            if(contact.getName().contains(s) || contact.getNumber().contains(s)) {
                                contacts.add(contact);
                            }
                        }
                    }
                }
                addressViewModel.setContactList(contacts);
            }

            @Override
            public void afterTextChanged(Editable s) {
                int textLength = s.length();
                int maxLength = 25;
                if (textLength > maxLength) {
                    // 如果超过限制，截取前面的限制字符
                    s.delete(maxLength, textLength);
                }
            }
        });
        return rootView;
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void init() {
        contactListAdapter = new ContactAdapter(addressViewModel.getContactList().getValue());
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
            if (addressViewModel != null) {
                addressViewModel.setContactList(contacts);
            }
            Global.setContactList(contacts);
            contactPb.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(int i) {
            contactPb.setVisibility(View.GONE);
        }

        @Override
        public void onSuccess(String s) {
            contactPb.setVisibility(View.GONE);
        }
    };

    public void setAddressViewModel(AddressViewModel addressViewModel) {
        this.addressViewModel = addressViewModel;
    }

    private void searchContact() {
        if(Global.connStatus != Global.CONNECTED) {
            return;
        }
//        bluetoothManager.stopContactOrHistoryLoad(stub1);
        if (bluetoothManager != null) {
            bluetoothManager.getPhoneContacts(stub);
            contactPb.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Global.connStatus == Global.NOT_CONNECTED) {
            addressViewModel.setCallLogList(new ArrayList<>());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (addressViewModel != null) {
                List<Contact> contacts = addressViewModel.getContactList().getValue();
                if (contacts != null && contacts.isEmpty()) {
                   searchContact();
                }
                if (Global.connStatus == Global.NOT_CONNECTED) {
                    addressViewModel.setContactList(new ArrayList<>());
                    Global.setContactList(new ArrayList<>());
                }
            }
        }
    }
}
