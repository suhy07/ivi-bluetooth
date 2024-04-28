package com.jancar.bluetooth.ui.address;


import android.content.res.Configuration;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jancar.bluetooth.BR;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.adapters.AddressTabPagerAdapter;
import com.jancar.bluetooth.app.BluetoothApplication;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * @author suhy
 */
public class AddressFragment extends BaseFragment {

    private final static String TAG = AddressFragment.class.getName();
    private AddressViewModel addressViewModel;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AddressTabPagerAdapter tabAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = binding.getRoot();
        initView(root);
        init();

        return root;
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_address;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public AddressViewModel initViewModel() {
        return new AddressViewModel(BluetoothApplication.getInstance());
    }

    private void initView(View root) {
        tabLayout = root.findViewById(R.id.tabLayout);
        viewPager = root.findViewById(R.id.viewPager);
    }

    private void init() {
        tabAdapter = new AddressTabPagerAdapter(getChildFragmentManager(), addressViewModel);
        viewPager.setAdapter(tabAdapter);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }
}