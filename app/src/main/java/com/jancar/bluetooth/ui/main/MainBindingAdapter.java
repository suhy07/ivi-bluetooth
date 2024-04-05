package com.jancar.bluetooth.ui.main;


import android.databinding.ViewDataBinding;
import android.view.ViewGroup;

import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter;

/**
 * @author suhy
 */
public class MainBindingAdapter extends BindingViewPagerAdapter<MainItemViewModel> {
    private final static String TAG = "MainFragmentPagerAdapter";

    public MainBindingAdapter() {
        super();
    }

    @Override
    public void onBindBinding(final ViewDataBinding binding, int variableId, int layoutRes,
                              final int position, MainItemViewModel item) {
        super.onBindBinding(binding, variableId, layoutRes, position, (MainItemViewModel) item);
        //这里可以强转成ViewPagerItemViewModel对应的ViewDataBinding，
//        ItemViewpagerBinding _binding = (ItemViewpagerBinding) binding;
    }

    @Override
    public int getCount() {
        // 根据你的需求返回Fragment数量
        return 3;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (position == 3) {
            return;
        }
        super.destroyItem(container, position, object);
    }
}