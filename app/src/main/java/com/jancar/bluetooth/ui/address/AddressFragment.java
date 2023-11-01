package com.jancar.bluetooth.ui.address;


import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jancar.bluetooth.R;
import com.jancar.bluetooth.adapters.AddressTabPagerAdapter;
import com.jancar.bluetooth.viewmodels.AddressViewModel;

/**
 * @author suhy
 */
public class AddressFragment extends Fragment {

    private AddressViewModel mViewModel;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AddressTabPagerAdapter tabAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_address, container, false);

        tabLayout = root.findViewById(R.id.tabLayout);
        viewPager = root.findViewById(R.id.viewPager);
        mViewModel =   new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory()).get(AddressViewModel.class);
        tabAdapter = new AddressTabPagerAdapter(getFragmentManager());
        viewPager.setAdapter(tabAdapter);
        // 使用 TabLayoutMediator 将 TabLayout 与 ViewPager 关联
        tabLayout.setupWithViewPager(viewPager);

        String[] tabTitles = {getString(R.string.title_contact), getString(R.string.title_call_log)};
        for (int i = 0; i < tabTitles.length; i++) {
            tabLayout.getTabAt(i).setText(tabTitles[i]);
        }

        return root;
    }
}