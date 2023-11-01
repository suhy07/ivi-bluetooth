package com.jancar.bluetooth;

import android.annotation.NonNull;
import android.arch.lifecycle.ViewModelProvider;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jancar.bluetooth.adapters.MainFragmentPagerAdapter;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.utils.BluetoothUtil;
import com.jancar.bluetooth.viewmodels.MainViewModel;

/**
 * @author suhy
 */
public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MainViewModel viewModel;
    private BottomNavigationView bottomNavigationView;
    String[] permissions = {
            android.Manifest.permission.BLUETOOTH,
            android. Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.BLUETOOTH_PRIVILEGED
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        init();

        // 检查是否已获得蓝牙权限
        for (String permission: permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 如果没有蓝牙权限，请求蓝牙权限
                Log.d("permission", permission);
                ActivityCompat.requestPermissions(this, new String[] { permission }, Global.REQUEST_ENABLE_BT);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Global.REQUEST_ENABLE_BT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限已被授予，可以执行需要权限的操作
            } else {
                // 权限被拒绝，可能需要向用户解释为什么需要这些权限
            }
        }
    }



    private void initView(){
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void init(){
        viewModel =   new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory()).get(MainViewModel.class);

        bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu);
        viewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager()));
        // 设置ViewPager的页面切换监听，以便更新BottomNavigationView的选中项
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                viewModel.setSelectedPage(position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_address) {
                viewPager.setCurrentItem(0, false);
                return true;
            } else if (item.getItemId() == R.id.nav_device) {
                viewPager.setCurrentItem(1, false);
                return true;
            } else if (item.getItemId() == R.id.nav_music) {
                viewPager.setCurrentItem(2, false);
                return true;
            } else if (item.getItemId() == R.id.nav_phone) {
                viewPager.setCurrentItem(3, false);
                return true;
            }
            return false;
        });
        viewModel.getSelectedPage().observe(this, position -> {
            bottomNavigationView.getMenu().getItem(position).setChecked(true);
        });
        BluetoothUtil.setContexta(this);
    }
}