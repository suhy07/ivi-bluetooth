package com.jancar.bluetooth.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.jancar.bluetooth.ui.address.AddressFragment;
import com.jancar.bluetooth.ui.device.DeviceFragment;
import com.jancar.bluetooth.ui.music.MusicFragment;
import com.jancar.bluetooth.ui.phone.PhoneFragment;

/**
 * @author suhy
 */
public class MainFragmentPagerAdapter extends FragmentStateAdapter {
    public MainFragmentPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AddressFragment();
            case 1:
                return new DeviceFragment();
            case 2:
                return new MusicFragment();
            case 3:
                return new PhoneFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}