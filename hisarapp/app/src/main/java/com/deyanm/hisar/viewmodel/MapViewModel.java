package com.deyanm.hisar.viewmodel;

import android.util.Log;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.deyanm.hisar.model.Place;
import com.deyanm.hisar.repository.Repository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapViewModel extends ViewModel {
    private static final String TAG = MapViewModel.class.getSimpleName();

    private Repository repository;
    private MutableLiveData<Place> place = new MutableLiveData<>();

    @ViewModelInject
    public MapViewModel(Repository repository) {
        this.repository = repository;
    }

    public MutableLiveData<Place> getPlace() {
        return place;
    }

    public void getCurrentPlace() {
        repository.getCurrentPlace()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(returnPlace -> place.setValue(returnPlace), error -> Log.d(TAG, "error getting place " + error.getMessage()));
    }

//    public void getPlaceFromUtil(Context context, int id) {
//        Utils.getJsonFromAppFolder(context)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(places -> place.setValue(places.get(0)), error -> Log.d(TAG, "error getting place " + error.getMessage()));
//    }

}