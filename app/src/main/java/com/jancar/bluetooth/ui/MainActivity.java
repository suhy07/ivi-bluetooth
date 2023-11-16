package com.jancar.bluetooth.ui;

import android.annotation.NonNull;
import android.arch.lifecycle.ViewModelProvider;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jancar.bluetooth.R;
import com.jancar.bluetooth.adapters.MainFragmentPagerAdapter;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.service.BluetoothService;
import com.jancar.bluetooth.ui.address.AddressFragment;
import com.jancar.bluetooth.ui.device.DeviceFragment;
import com.jancar.bluetooth.ui.music.MusicFragment;
import com.jancar.bluetooth.ui.phone.PhoneFragment;
import com.jancar.bluetooth.utils.BluetoothUtil;
import com.jancar.bluetooth.viewmodels.AddressViewModel;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;
import com.jancar.bluetooth.viewmodels.MainViewModel;
import com.jancar.bluetooth.viewmodels.MusicViewModel;
import com.jancar.bluetooth.viewmodels.PhoneViewModel;


import java.util.HashSet;
import java.util.Set;

/**
 * @author suhy
 */
public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private NoPreloadViewPager viewPager;
    private MainViewModel mainViewModel;
    private DeviceViewModel deviceViewModel;
    private AddressViewModel addressViewModel;
    private MusicViewModel musicViewModel;
    private PhoneViewModel phoneViewModel;
    private DeviceFragment deviceFragment;
    private AddressFragment addressFragment;
    private MusicFragment musicFragment;
    private PhoneFragment phoneFragment;
    private BluetoothService bluetoothService;
    private ServiceConnection serviceConnection;
    private TabLayout tabLayout;
    private final String[] permissions = {
            android.Manifest.permission.BLUETOOTH,
            android. Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.BLUETOOTH_PRIVILEGED
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
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
        // 检查是否有已连接的蓝牙
        Set<BluetoothDevice> deviceSet = new HashSet<>(BluetoothUtil.getBondedDevices());
        Global.connStatus = Global.NOT_CONNECTED;
        for (BluetoothDevice bluetoothDevice : deviceSet) {
            if(bluetoothDevice.isConnected()) {
                Global.connStatus = Global.CONNECTED;
                break;
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
        tabLayout = findViewById(R.id.tabLayout_main);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColors(new int[]{0xFF302642, 0xFF1b213d});
        gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        viewPager.setBackground(gradientDrawable);

    }

    private void init(){
        String[] tabTitles = {getString(R.string.title_music), getString(R.string.title_address),
                getString(R.string.title_phone), getString(R.string.str_title_device)};
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        for (int i = 0; i < tabTitles.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tabTitles[i]), (i == 0));
        }
        mainViewModel = new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory()).get(MainViewModel.class);
        deviceViewModel = new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory()).get(DeviceViewModel.class);
        addressViewModel = new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory()).get(AddressViewModel.class);
        musicViewModel = new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory()).get(MusicViewModel.class);
        phoneViewModel = new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory()).get(PhoneViewModel.class);

        viewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager(), deviceViewModel, addressViewModel
                , musicViewModel, phoneViewModel));
        // 设置 OffscreenPageLimit 为 0，禁用预加载
        viewPager.setOffscreenPageLimit(0);
        // 设置ViewPager的页面切换监听，以便更新BottomNavigationView的选中项
        viewPager.setOnPageChangeListener(new NoPreloadViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                mainViewModel.setSelectedPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i(TAG, "position:" + tab.getPosition());
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mainViewModel.getSelectedPage().observe(this, position -> {
            if(tabLayout.getTabAt(position) != null) {
                tabLayout.getTabAt(position).select();
            }
        });
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) iBinder;
                bluetoothService = binder.getService();
                bluetoothService.setDeviceViewModel(deviceViewModel);
                bluetoothService.setAddressViewModel(addressViewModel);
                bluetoothService.setMusicViewModel(musicViewModel);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                bluetoothService = null;
            }
        };
        Intent serviceIntent = new Intent(MainActivity.this, BluetoothService.class);
        // 启动服务
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}