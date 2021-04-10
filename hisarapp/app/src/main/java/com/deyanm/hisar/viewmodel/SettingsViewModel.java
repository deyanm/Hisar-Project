package com.deyanm.hisar.viewmodel;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.deyanm.hisar.model.Place;
import com.deyanm.hisar.repository.Repository;

import java.util.ArrayList;

public class SettingsViewModel extends ViewModel {
    private static final String TAG = SettingsViewModel.class.getSimpleName();

    private Repository repository;
    private MutableLiveData<ArrayList<Place>> placesList = new MutableLiveData<>();

    @ViewModelInject
    public SettingsViewModel(Repository repository) {
        this.repository = repository;
    }

    public MutableLiveData<ArrayList<Place>> getPlacesList() {
        return placesList;
    }
}