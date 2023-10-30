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

import java.util.ArrayList;
import java.util.List;

/**
 * @author suhy
 */
public class ContactListFragment extends Fragment {

    private ContactAdapter contactListAdapter;
    private RecyclerView recyclerView;
    private List<Contact> contactList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        for(int i = 0; i < 10; i++){
            contactList.add(new Contact(""+ i, ""+i));
        }
        recyclerView = rootView.findViewById(R.id.rv_contact);
        contactListAdapter = new ContactAdapter(contactList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(contactListAdapter);
        return rootView;
    }


}
