package com.deyanm.hisar.repository;

import android.util.Log;

import com.deyanm.hisar.db.HisarDao;
import com.deyanm.hisar.model.Place;
import com.deyanm.hisar.model.PlaceResponse;
import com.deyanm.hisar.network.ApiService;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;

public class Repository {
    private static final String TAG = Repository.class.getSimpleName();

    ApiService apiService;
    HisarDao hisarDao;


    @Inject
    public Repository(ApiService apiService, HisarDao hisarDao) {
        this.apiService = apiService;
        this.hisarDao = hisarDao;
    }

    public Observable<PlaceResponse> getPlaces() {
        return apiService.getPlaces();
    }

    public Observable<PlaceResponse> getPlace(String id) {
        return apiService.getPlace(id);
    }

    public void insertPlace(Place place) {
        Log.e(TAG, "insertMovie: ");
        hisarDao.insertPlace(place);
    }

    public void deletePlace(int id) {
        hisarDao.deletePlace(id);
    }
}
