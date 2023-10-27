package com.jancar.bluetooth.ui.address;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jancar.bluetooth.R;
import com.jancar.bluetooth.viewmodels.AddressViewModel;

/**
 * @author suhy
 */
public class AddressFragment extends Fragment {

    private AddressViewModel mViewModel;

    public static AddressFragment newInstance() {
        return new AddressFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_address, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddressViewModel.class);
        // TODO: Use the ViewModel
    }

}