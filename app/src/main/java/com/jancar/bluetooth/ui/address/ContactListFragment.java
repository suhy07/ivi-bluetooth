package com.jancar.bluetooth.ui.address;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jancar.bluetooth.R;
import com.jancar.bluetooth.adapters.ContactAdapter;
import com.jancar.bluetooth.model.Contact;
import com.jancar.bluetooth.viewmodels.AddressViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suhy
 */
public class ContactListFragment extends Fragment {

    private ContactAdapter contactListAdapter;
    private RecyclerView recyclerView;
    private List<Contact> contactList = new ArrayList<>();
    private AddressViewModel addressViewModel;

    public ContactListFragment(AddressViewModel addressViewModel){
        this.addressViewModel = addressViewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        recyclerView = rootView.findViewById(R.id.rv_contact);
        contactListAdapter = new ContactAdapter(contactList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(contactListAdapter);
        addressViewModel.getContactList().observe(getViewLifecycleOwner(), contacts -> {
            contactListAdapter.setContactList(contactList);
            contactListAdapter.notifyDataSetChanged();
        });
        return rootView;
    }

    public void setAddressViewModel(AddressViewModel addressViewModel) {
        this.addressViewModel = addressViewModel;
    }

}
