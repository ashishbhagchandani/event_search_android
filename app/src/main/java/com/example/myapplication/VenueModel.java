package com.example.myapplication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VenueModel extends ViewModel {
    private MutableLiveData<String> venue = new MutableLiveData<>();

    public void setData(String data) {
        venue.setValue(data);
    }

    public LiveData<String> getData() {
        return venue;
    }
}