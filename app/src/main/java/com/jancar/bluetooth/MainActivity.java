package com.jancar.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.jancar.bluetooth.adapters.MainFragmentPagerAdapter;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.viewmodels.MainViewModel;

/**
 * @author suhy
 */
public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private MainViewModel viewModel;
    private BottomNavigationView bottomNavigationView;
    private final Global global = Global.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        init();
    // 检查是否已获得蓝牙权限
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有蓝牙权限，请求蓝牙权限

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH}, global.REQUEST_ENABLE_BT);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == global.REQUEST_ENABLE_BT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了蓝牙权限，可以继续执行相关操作
            } else {
                // 用户拒绝了蓝牙权限，需要处理拒绝的情况
            }
        }
    }


    private void initView(){
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void init(){
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu);
        viewPager.setAdapter(new MainFragmentPagerAdapter(this));
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // 更新底部导航栏的选中项
                viewModel.setSelectedPage(position);
            }
        });
        viewModel.getSelectedPage().observe(this, position -> {
            bottomNavigationView.getMenu().getItem(position).setChecked(true);
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
    }

    private class BluetoothManager {
    }
}