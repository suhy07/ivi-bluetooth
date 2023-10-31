package com.jancar.bluetooth.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.jancar.bluetooth.ui.address.CallLogFragment;
import com.jancar.bluetooth.ui.address.ContactListFragment;
import com.jancar.bluetooth.viewmodels.AddressViewModel;

/**
 * @author suhy
 */
public class AddressTabPagerAdapter extends FragmentStateAdapter {

    private AddressViewModel addressViewModel;
    public AddressTabPagerAdapter(Fragment fragment, AddressViewModel addressViewModel) {
        super(fragment);
        this.addressViewModel = addressViewModel;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new ContactListFragment(addressViewModel);
        } else if (position == 1) {
            return new CallLogFragment(addressViewModel);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
