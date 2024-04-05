package com.jancar.bluetooth.ui.main;


import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import com.jancar.bluetooth.ui.address.AddressFragment;
import com.jancar.bluetooth.ui.device.DeviceFragment;
import com.jancar.bluetooth.ui.music.MusicFragment;
import com.jancar.bluetooth.ui.phone.PhoneFragment;
import com.jancar.bluetooth.ui.address.AddressViewModel;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;
import com.jancar.bluetooth.ui.music.MusicViewModel;
import com.jancar.bluetooth.viewmodels.PhoneViewModel;

import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter;

/**
 * @author suhy
 */
public class MainBindingAdapter extends BindingViewPagerAdapter<MainItemViewModel> {
    private final static String TAG = "MainFragmentPagerAdapter";
    private DeviceViewModel deviceViewModel;
    private AddressViewModel addressViewModel;
    private MusicViewModel musicViewModel;
    private PhoneViewModel phoneViewModel;
    private DeviceFragment deviceFragment;
    private AddressFragment addressFragment;
    private MusicFragment musicFragment;
    private PhoneFragment phoneFragment;


    public MainBindingAdapter() {
        super();
    }

    public MainBindingAdapter(FragmentManager fm, DeviceViewModel deviceViewModel,
                              AddressViewModel addressViewModel, MusicViewModel musicViewModel,
                              PhoneViewModel phoneViewModel) {
        super();
        this.deviceViewModel = deviceViewModel;
        this.addressViewModel = addressViewModel;
        this.musicViewModel = musicViewModel;
        this.phoneViewModel = phoneViewModel;
    }

    @Override
    public void onBindBinding(final ViewDataBinding binding, int variableId, int layoutRes,
                              final int position, MainItemViewModel item) {
        super.onBindBinding(binding, variableId, layoutRes, position, item);
        //这里可以强转成ViewPagerItemViewModel对应的ViewDataBinding，
//        ItemViewpagerBinding _binding = (ItemViewpagerBinding) binding;
    }

    @Override
    public int getCount() {
        // 根据你的需求返回Fragment数量
        return 4;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (position == 3) {
            return;
        }
        super.destroyItem(container, position, object);
    }
}