package com.jancar.bluetooth.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.jancar.bluetooth.ui.address.AddressFragment;
import com.jancar.bluetooth.ui.device.DeviceFragment;
import com.jancar.bluetooth.ui.music.MusicFragment;
import com.jancar.bluetooth.ui.phone.PhoneFragment;
import com.jancar.bluetooth.viewmodels.AddressViewModel;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;
import com.jancar.bluetooth.viewmodels.MusicViewModel;
import com.jancar.bluetooth.viewmodels.PhoneViewModel;

/**
 * @author suhy
 */
public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    private DeviceViewModel deviceViewModel;
    private AddressViewModel addressViewModel;
    private MusicViewModel musicViewModel;
    private PhoneViewModel phoneViewModel;
    private DeviceFragment deviceFragment;
    private AddressFragment addressFragment;
    private MusicFragment musicFragment;
    private PhoneFragment phoneFragment;

    public MainFragmentPagerAdapter(FragmentManager fm, DeviceViewModel deviceViewModel,
                                    AddressViewModel addressViewModel, MusicViewModel musicViewModel,
                                    PhoneViewModel phoneViewModel) {
        super(fm);
        this.deviceViewModel = deviceViewModel;
        this.addressViewModel = addressViewModel;
        this.musicViewModel = musicViewModel;
        this.phoneViewModel = phoneViewModel;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MusicFragment musicFragment = new MusicFragment();
                musicFragment.setMusicViewModel(musicViewModel);
                return musicFragment;
            case 1:
                AddressFragment addressFragment = new AddressFragment();
                addressFragment.setAddressViewModel(addressViewModel);
                return addressFragment;
            case 2:
                PhoneFragment phoneFragment = new PhoneFragment();
                phoneFragment.setPhoneViewModel(phoneViewModel);
                return phoneFragment;
            case 3:
                DeviceFragment deviceFragment = new DeviceFragment();
                deviceFragment.setDeviceViewModel(deviceViewModel);
                return deviceFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // 根据你的需求返回Fragment数量
        return 4;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
    }
}