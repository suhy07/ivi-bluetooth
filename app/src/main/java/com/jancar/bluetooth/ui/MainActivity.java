package com.jancar.bluetooth.ui;

import android.annotation.NonNull;
import android.arch.lifecycle.ViewModelProvider;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jancar.bluetooth.MainApplication;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.adapters.MainFragmentPagerAdapter;
import com.jancar.bluetooth.global.Global;
import com.jancar.bluetooth.service.BluetoothService;
import com.jancar.bluetooth.utils.BluetoothUtil;
import com.jancar.bluetooth.viewmodels.AddressViewModel;
import com.jancar.bluetooth.viewmodels.DeviceViewModel;
import com.jancar.bluetooth.viewmodels.MainViewModel;
import com.jancar.bluetooth.viewmodels.MusicViewModel;
import com.jancar.bluetooth.viewmodels.PhoneViewModel;
import com.jancar.sdk.bluetooth.BluetoothManager;
import com.jancar.sdk.bluetooth.IVIBluetooth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    private BluetoothService bluetoothService;
    private ServiceConnection serviceConnection;
    private BottomNavigationView bottomNavigationView;
    private BluetoothManager bluetoothManager;
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
        for (BluetoothDevice bluetoothDevice : deviceSet) {
            if(bluetoothDevice.isConnected()) {
                Global.connStatus = Global.CONNECTED;
                break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventPhoneStatus(IVIBluetooth.CallStatus event) {
        /*Log.i(TAG, event.toString());
        if(event.mStatus == IVIBluetooth.CallStatus.INCOMING ||
                event.mStatus == IVIBluetooth.CallStatus.OUTGOING) {
            boolean isComing = (event.mStatus == IVIBluetooth.CallStatus.INCOMING);
            String number = event.mPhoneNumber;
            String name = Global.findNameByNumber(number);
            Intent intent = new Intent(MainActivity.this, CallActivity.class);
            intent.putExtra(Global.EXTRA_IS_COMING, isComing);
            intent.putExtra(Intent.EXTRA_PHONE_NUMBER, number);
            intent.putExtra(Global.EXTRA_NAME, name);
            startActivity(intent);
        }*/
    }

    private void initView(){
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void init(){
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
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
        bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu);
        viewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager(), deviceViewModel, addressViewModel
                , musicViewModel, phoneViewModel));
        // 设置 OffscreenPageLimit 为 1，禁用预加载
        viewPager.setOffscreenPageLimit(0);
        // 设置ViewPager的页面切换监听，以便更新BottomNavigationView的选中项
        viewPager.setOnPageChangeListener(new NoPreloadViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mainViewModel.setSelectedPage(position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                mainViewModel.setSelectedPage(position);
//                bottomNavigationView.getMenu().getItem(position).setChecked(true);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_device) {
                viewPager.setCurrentItem(0, false);
                return true;
            } else if (item.getItemId() == R.id.nav_address) {
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

        mainViewModel.getSelectedPage().observe(this, position -> {
            bottomNavigationView.getMenu().getItem(position).setChecked(true);
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
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbindService(serviceConnection);
    }
}