package com.jancar.bluetooth.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.ui.address.CallLogFragment;
import com.jancar.bluetooth.ui.address.ContactListFragment;
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

    String[] tabTitles = {MainApplication.getInstance().getString(R.string.title_contact),
            MainApplication.getInstance().getString(R.string.title_call_log)};

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
    }
}
