package com.jancar.bluetooth.ui.main;

import android.support.annotation.NonNull;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

public class MainItemViewModel extends ItemViewModel<MainViewModel> {

    public MainItemViewModel(@NonNull MainViewModel viewModel) {
        super(viewModel);
    }

    public BindingCommand onItemClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            //点击之后将逻辑转到activity中处理
//            viewModel.itemClickEvent.setValue(text);
        }
    });
}