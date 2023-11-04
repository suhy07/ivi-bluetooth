package com.jancar.bluetooth.adapters;




import android.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jancar.bluetooth.ui.address.AddressFragment;
import com.jancar.bluetooth.ui.address.CallLogFragment;
import com.jancar.bluetooth.ui.address.ContactListFragment;
import com.jancar.bluetooth.ui.device.DeviceFragment;
import com.jancar.bluetooth.ui.music.MusicFragment;
import com.jancar.bluetooth.ui.phone.PhoneFragment;
import com.jancar.bluetooth.viewmodels.AddressViewModel;


/**
 * @author suhy
 */
public class AddressTabPagerAdapter extends FragmentPagerAdapter {

    private AddressViewModel addressViewModel;

    public AddressTabPagerAdapter(FragmentManager fm, AddressViewModel addressViewModel) {
        super(fm);
        this.addressViewModel = addressViewModel;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ContactListFragment contactListFragment = new ContactListFragment();
                contactListFragment.setAddressViewModel(addressViewModel);
                return contactListFragment;
            case 1:
                CallLogFragment callLogFragment = new CallLogFragment();
                callLogFragment.setAddressViewModel(addressViewModel);
                return callLogFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
