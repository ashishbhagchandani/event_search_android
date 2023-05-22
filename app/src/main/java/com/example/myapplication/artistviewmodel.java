package com.example.myapplication;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.Closeable;

public class artistviewmodel extends ViewModel {
    private MutableLiveData<String> data;

    public artistviewmodel(Application application) {
        super((Closeable) application);
        data = new MutableLiveData<>();
    }

    public void setData(String value) {
        data.setValue(value);
    }

    public LiveData<String> getData() {
        return data;
    }
}
