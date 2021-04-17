package com.deyanm.hisar.viewmodel;

import android.util.Log;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.deyanm.hisar.model.Place;
import com.deyanm.hisar.repository.Repository;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class MainViewModel extends ViewModel {
    private static final String TAG = MainViewModel.class.getSimpleName();

    private Repository repository;
    private MutableLiveData<ArrayList<Place>> placesList = new MutableLiveData<>();
    private MutableLiveData<Integer> version = new MutableLiveData<>();
    private MutableLiveData<ResponseBody> fileResponse = new MutableLiveData<>();

    @ViewModelInject
    public MainViewModel(Repository repository) {
        this.repository = repository;
    }

    public MutableLiveData<ResponseBody> getFileResponse() {
        return fileResponse;
    }

    public MutableLiveData<ArrayList<Place>> getPlacesList() {
        return placesList;
    }

    public MutableLiveData<Integer> getVersion() {
        return version;
    }

    public void getPlaces() {
        repository.getPlaces()
                .subscribeOn(Schedulers.io())
                .map(placeResponse -> placeResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> placesList.setValue(result),
                        error -> Log.e(TAG, "error getting places " + error.getMessage()));
    }

    public void getVersions() {
        repository.getVersions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(responseBody -> {
                    JsonArray jsonArray = new JsonParser().parse(responseBody.string()).getAsJsonArray();
                    return Observable.just(jsonArray.get(0).getAsJsonObject().get("current").getAsInt());
                })
                .subscribe(result -> version.setValue(result),
                        error -> Log.e(TAG, "error getting db file version: " + error.getMessage()));
    }

    public void downloadFile(String url) {
        repository.getFile(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(responseBody -> Observable.just(responseBody.body()))
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        fileResponse.setValue(responseBody);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        Log.d(TAG, "Error " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onCompleted downloading the file");
                    }
                });
    }

    public void insertPlace(Place place) {
        repository.insertPlace(place);
    }

    public void deletePlace(int id) {
        repository.deletePlace(id);
    }
}