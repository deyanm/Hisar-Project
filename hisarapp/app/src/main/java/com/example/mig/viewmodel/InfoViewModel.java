package com.example.mig.viewmodel;

import android.util.Log;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mig.model.Place;
import com.example.mig.repository.Repository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class InfoViewModel extends ViewModel {
    private static final String TAG = InfoViewModel.class.getSimpleName();

    private Repository repository;
    private MutableLiveData<Place> place = new MutableLiveData<>();

    @ViewModelInject
    public InfoViewModel(Repository repository) {
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

    public String getLangLocale() {
        return repository.getLanguageLocale();
    }

}