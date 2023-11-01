package com.jancar.bluetooth.ui.phone;

import android.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.jancar.bluetooth.R;

/**
 * @author suhy
 */
public class PhoneFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_phone, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}