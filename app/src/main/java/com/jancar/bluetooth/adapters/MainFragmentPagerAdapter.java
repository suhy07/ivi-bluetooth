package com.jancar.bluetooth.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jancar.bluetooth.ui.address.AddressFragment;
import com.jancar.bluetooth.ui.device.DeviceFragment;
import com.jancar.bluetooth.ui.music.MusicFragment;
import com.jancar.bluetooth.ui.phone.PhoneFragment;

/**
 * @author suhy
 */
public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    public MainFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DeviceFragment();

            case 1:
                return new AddressFragment();
            case 2:
                return new MusicFragment();
            case 3:
                return new PhoneFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // 根据你的需求返回Fragment数量
        return 4;
    }
}