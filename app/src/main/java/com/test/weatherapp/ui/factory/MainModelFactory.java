package com.test.weatherapp.ui.factory;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.test.weatherapp.data.repository.MainRepository;
import com.test.weatherapp.ui.activity.MainActivity;
import com.test.weatherapp.ui.viewmodel.MainViewModel;

public class MainModelFactory implements ViewModelProvider.Factory {
    Context ctx;
    public MainModelFactory(MainActivity mainActivity) {
        this.ctx=mainActivity;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(MainViewModel.class)){
        return (T) new MainViewModel(MainRepository.getInstance(),ctx);
    }else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
