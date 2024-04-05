package com.jancar.bluetooth.ui.main;

import android.annotation.NonNull;
import android.app.Application;
import android.app.Fragment;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.jancar.bluetooth.BR;
import com.jancar.bluetooth.R;
import com.jancar.bluetooth.ui.address.AddressFragment;
import com.jancar.bluetooth.ui.device.DeviceFragment;
import com.jancar.bluetooth.ui.music.MusicFragment;
import com.jancar.bluetooth.ui.music.MusicViewModel;
import com.jancar.bluetooth.ui.phone.PhoneFragment;

import java.util.Arrays;

import me.goldze.mvvmhabit.base.BaseModel;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

/**
 * @author suhy
 */
public class MainViewModel extends BaseViewModel {
    private MutableLiveData<Integer> selectedPage = new MutableLiveData<>();
    private Object[] pages = new Object[] {
            new MusicFragment(),
            new AddressFragment(),
            new PhoneFragment(),
            new DeviceFragment()
    };

    final private String[] titles = {
            getApplication().getString(R.string.title_music), getApplication().getString(R.string.title_address),
            getApplication().getString(R.string.title_phone), getApplication().getString(R.string.str_title_device)
    };

    public ObservableList<ItemViewModel> items = new ObservableArrayList<>();
    public ItemBinding<MainItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_viewpager);

    public MainViewModel(@NonNull Application application) {
        super(application);
        selectedPage = new MutableLiveData<>();
        selectedPage.setValue(0);
        items.add(new MainItemViewModel(this));
        items.add(new MainItemViewModel(this));
        items.add(new MainItemViewModel(this));
        items.add(new MainItemViewModel(this));
//        items.add(new MainItemViewModel(this));
//        items.add(new MainItemViewModel(this));
//        items.add(new MainItemViewModel(this));
    }

    public final BindingViewPagerAdapter.PageTitles<MainItemViewModel> pageTitles =
            (position, item) -> titles[position];

    //ViewPager切换监听
    public BindingCommand<Integer> onPageSelectedCommand = new BindingCommand<>(new BindingConsumer<Integer>() {
        @Override
        public void call(Integer position) {
            setSelectedPage(position);
//            switch (position) {
//                case 0:
//                    itemBinding = ItemBinding.of(BR.viewModel, R.layout.fragment_music);
//                    break;
//                case 1:
//                    itemBinding = ItemBinding.of(BR.viewModel, R.layout.fragment_address);
//                    break;
//                case 2:
//                    itemBinding = ItemBinding.of(BR.viewModel, R.layout.fragment_phone);
//                    break;
//                case 3:
//                    itemBinding = ItemBinding.of(BR.viewModel, R.layout.fragment_device);
//                    break;
//                default:
//                    itemBinding = ItemBinding.of(BR.viewModel, R.layout.fragment_music);
//            }
        }
    });

    public LiveData<Integer> getSelectedPage() {
        return selectedPage;
    }

    public void setSelectedPage(int position) {
        selectedPage.setValue(position);
    }
}
