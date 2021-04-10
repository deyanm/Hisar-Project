package com.deyanm.hisar.viewmodel;

import android.util.Log;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.deyanm.hisar.model.Place;
import com.deyanm.hisar.repository.Repository;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends ViewModel {
    private static final String TAG = MainViewModel.class.getSimpleName();

    private Repository repository;
    private MutableLiveData<ArrayList<Place>> placesList = new MutableLiveData<>();

    @ViewModelInject
    public MainViewModel(Repository repository) {
        this.repository = repository;
    }

    public MutableLiveData<ArrayList<Place>> getPlacesList() {
        return placesList;
    }

    public void getPlaces() {
        repository.getPlaces()
                .subscribeOn(Schedulers.io())
                .map(placeResponse -> {
                    return placeResponse;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> placesList.setValue(result),
                        error -> Log.e(TAG, "getPokemons: " + error.getMessage()));
    }

    public void insertPlace(Place place) {
        repository.insertPlace(place);
    }

    public void deletePlace(int id) {
        repository.deletePlace(id);
    }
}