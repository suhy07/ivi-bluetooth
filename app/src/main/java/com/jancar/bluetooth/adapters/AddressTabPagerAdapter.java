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

    public AddressTabPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ContactListFragment();
            case 1:
                return new CallLogFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
