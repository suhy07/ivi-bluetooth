package com.jancar.bluetooth.ui.address;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.adapters.AddressTabPagerAdapter;
import com.jancar.bluetooth.viewmodels.AddressViewModel;

/**
 * @author suhy
 */
public class AddressFragment extends Fragment {

    private AddressViewModel mViewModel;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private AddressTabPagerAdapter tabAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_address, container, false);

        tabLayout = root.findViewById(R.id.tabLayout);
        viewPager = root.findViewById(R.id.viewPager);
        mViewModel = new ViewModelProvider(this).get(AddressViewModel.class);
        tabAdapter = new AddressTabPagerAdapter(this, mViewModel);
        viewPager.setAdapter(tabAdapter);

        // 使用 TabLayoutMediator 将 TabLayout 与 ViewPager2 关联
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText(getText(R.string.title_contact));
                    } else if (position == 1) {
                        tab.setText(getText(R.string.title_call_log));
                    }
                }
        ).attach();

        return root;
    }
}