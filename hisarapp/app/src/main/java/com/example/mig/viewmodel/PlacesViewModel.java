package com.example.mig.viewmodel;

import android.util.Log;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mig.model.Poi;
import com.example.mig.repository.Repository;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlacesViewModel extends ViewModel {
    private static final String TAG = PlacesViewModel.class.getSimpleName();

    private Repository repository;
    private MutableLiveData<List<Poi>> poisList = new MutableLiveData<>();
//    private MutableLiveData<ArrayList<Restaurant>> restList = new MutableLiveData<>();
//    private MutableLiveData<ArrayList<Hotel>> hotelsList = new MutableLiveData<>();

    @ViewModelInject
    public PlacesViewModel(Repository repository) {
        this.repository = repository;
    }

    public MutableLiveData<List<Poi>> getPois() {
        return poisList;
    }
//    public MutableLiveData<ArrayList<Restaurant>> getRestList() {
//        return restList;
//    }
//    public MutableLiveData<ArrayList<Hotel>> getHotelsList() {
//        return hotelsList;
//    }

    public void getPoisByType(String type) {
        repository.getPoisByType(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pois -> poisList.setValue(pois), error -> Log.d(TAG, "error getting place " + error.getMessage()));
    }

//    public void getRestaurants() {
//        repository.getRestaurants()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(rests -> restList.setValue(rests), error -> Log.d(TAG, "error getting place " + error.getMessage()));
//    }
//
//    public void getHotels() {
//        repository.getHotels()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(hotels -> hotelsList.setValue(hotels), error -> Log.d(TAG, "error getting place " + error.getMessage()));
//    }
}