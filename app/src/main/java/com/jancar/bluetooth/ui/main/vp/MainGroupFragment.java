package com.jancar.bluetooth.ui.main.vp;

import android.support.v4.app.Fragment;

import com.jancar.bluetooth.ui.address.AddressFragment;
import com.jancar.bluetooth.ui.base.fragment.BasePagerFragment;
import com.jancar.bluetooth.ui.device.DeviceFragment;
import com.jancar.bluetooth.ui.main.fragment.TabBar1Fragment;
import com.jancar.bluetooth.ui.main.fragment.TabBar2Fragment;
import com.jancar.bluetooth.ui.main.fragment.TabBar3Fragment;
import com.jancar.bluetooth.ui.main.fragment.TabBar4Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Create Author：goldze
 * Create Date：2019/01/25
 * Description：ViewPager+Fragment的实现
 */

public class MainGroupFragment extends BasePagerFragment {
    @Override
    protected List<Fragment> pagerFragment() {
        List<Fragment> list = new ArrayList<>();
        list.add(new TabBar1Fragment());
        list.add(new TabBar2Fragment());
        list.add(new TabBar3Fragment());
        list.add(new TabBar4Fragment());
        return list;
    }

    @Override
    protected List<String> pagerTitleString() {
        List<String> list = new ArrayList<>();
        list.add("page1");
//        list.add("page2");
//        list.add("page3");
//        list.add("page4");
        return list;
    }

}
