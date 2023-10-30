package com.jancar.bluetooth.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.jancar.bluetooth.ui.address.CallLogFragment;
import com.jancar.bluetooth.ui.address.ContactListFragment;

/**
 * @author suhy
 */
public class AddressTabPagerAdapter extends FragmentStateAdapter {

    public AddressTabPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new ContactListFragment();
        } else if (position == 1) {
            return new CallLogFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
