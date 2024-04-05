package com.jancar.bluetooth.ui.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.jancar.bluetooth.BR;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.databinding.ActivityMainBinding;
import com.jancar.bluetooth.ui.main.vp.MainGroupFragment;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * @author suhy
 */
public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    @Override
    public void initData() {
        // 使用 TabLayout 和 ViewPager 相关联
        binding.tabLayout.setupWithViewPager((ViewPager) binding.viewPager);
        ((ViewPager)(binding.viewPager)).addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
        //给ViewPager设置adapter
        binding.setAdapter(new MainBindingAdapter());
        startContainerActivity(MainGroupFragment.class.getCanonicalName());
    }

    @Override
    public void initViewObservable() {
//        viewModel.itemClickEvent.observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String text) {
//                ToastUtils.showShort("position：" + text);
//            }
//        });
    }
}