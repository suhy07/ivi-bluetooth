package com.jancar.bluetooth.ui.main.vp;

import android.support.v4.app.Fragment;

import com.jancar.bluetooth.R;
import com.jancar.bluetooth.ui.base.fragment.BasePagerFragment;
import com.jancar.bluetooth.ui.device.DeviceFragment;

import java.util.Arrays;
import java.util.List;

public class MainGroupFragment extends BasePagerFragment {
    private final Fragment[] pagerList = {
//            new MusicFragment(),
//            new AddressFragment(),
//            new PhoneFragment(),
            new DeviceFragment()
    };

    @Override
    protected List<Fragment> pagerFragment() {
        return Arrays.asList(pagerList);
    }

    @Override
    protected List<String> pagerTitleString() {
        String[] pagerTitleList = new String[]{
//                getString(R.string.title_music),
//                getString(R.string.title_address),
//                getString(R.string.title_phone),
                getString(R.string.title_device)
        };
        return Arrays.asList(pagerTitleList);
    }

}
