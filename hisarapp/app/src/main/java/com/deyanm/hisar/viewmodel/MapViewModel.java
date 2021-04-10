package com.deyanm.hisar.viewmodel;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.deyanm.hisar.model.Place;
import com.deyanm.hisar.repository.Repository;

import java.util.ArrayList;

public class MapViewModel extends ViewModel {
    private static final String TAG = MapViewModel.class.getSimpleName();

    private Repository repository;
    private MutableLiveData<ArrayList<Place>> placesList = new MutableLiveData<>();

    @ViewModelInject
    public MapViewModel(Repository repository) {
        this.repository = repository;
    }

    public MutableLiveData<ArrayList<Place>> getPlacesList() {
        return placesList;
    }
}