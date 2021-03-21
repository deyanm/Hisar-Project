package com.deyanm.hisar.viewmodel;

import android.util.Log;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.deyanm.hisar.model.Place;
import com.deyanm.hisar.model.PlaceResponse;
import com.deyanm.hisar.repository.Repository;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Function;
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

    public void getPokemons() {
        repository.getPlaces()
                .subscribeOn(Schedulers.io())
                .map(new Function<PlaceResponse, ArrayList<Place>>() {
                    @Override
                    public ArrayList<Place> apply(PlaceResponse pokemonResponse) throws Throwable {
                        ArrayList<Place> list = pokemonResponse.getResults();
                        for (Place place : list) {
                            String url = place.getUrl();
                            String[] pokemonIndex = url.split("/");
                            place.setUrl("https://pokeres.bastionbot.org/images/pokemon/" + pokemonIndex[pokemonIndex.length - 1] + ".png");
                        }
                        Log.e(TAG, "apply: " + list.get(2).getUrl());
                        return list;
                    }
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