package com.example.mig.viewmodel;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mig.model.Place;
import com.example.mig.repository.Repository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class MapViewModel extends ViewModel {
    private static final String TAG = MapViewModel.class.getSimpleName();

    private Repository repository;
    private MutableLiveData<Place> place = new MutableLiveData<>();
    private MutableLiveData<Location> location = new MutableLiveData<>();

    @Inject
    public MapViewModel(Repository repository) {
        this.repository = repository;
    }

    public MutableLiveData<Place> getPlace() {
        return place;
    }

    public void getCurrentPlace(String langCode) {
        repository.getCurrentPlace(langCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(returnPlace -> place.setValue(returnPlace), error -> Log.d(TAG, "error getting place " + error.getMessage()));
    }

    public MutableLiveData<Location> getLocation() {
        return location;
    }

    public String getLangLocale() {
        return repository.getLanguageLocale();
    }

    public int getCurrentPlaceId() {
        return repository.getCurrentPlaceId();
    }

//    public void getPlaceFromUtil(Context context, int id) {
//        Utils.getJsonFromAppFolder(context)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(places -> place.setValue(places.get(0)), error -> Log.d(TAG, "error getting place " + error.getMessage()));
//    }

}