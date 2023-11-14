package com.jancar.bluetooth.ui.address;


import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;

import android.os.RemoteException;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.adapters.AddressTabPagerAdapter;
import com.jancar.bluetooth.model.Contact;
import com.jancar.bluetooth.utils.BluetoothUtil;
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
public class AddressFragment extends Fragment {

    private final static String TAG = AddressFragment.class.getName();
    private AddressViewModel addressViewModel;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AddressTabPagerAdapter tabAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_address, container, false);
        initView(root);
        init();

        return root;
    }

    private void initView(View root) {
        tabLayout = root.findViewById(R.id.tabLayout);
        viewPager = root.findViewById(R.id.viewPager);
    }

    private void init() {
        tabAdapter = new AddressTabPagerAdapter(getChildFragmentManager(), addressViewModel);
        viewPager.setAdapter(tabAdapter);
        // 设置 OffscreenPageLimit 为 1，禁用预加载
        viewPager.setOffscreenPageLimit(0);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        // 使用 TabLayoutMediator 将 TabLayout 与 ViewPager 关联
        tabLayout.setupWithViewPager(viewPager);
        String[] tabTitles = {getString(R.string.title_contact), getString(R.string.title_call_log)};
        for (int i = 0; i < tabTitles.length; i++) {
            tabLayout.getTabAt(i).setText(tabTitles[i]);
        }
    }

    public void setAddressViewModel(AddressViewModel addressViewModel) {
        this.addressViewModel = addressViewModel;
    }
}