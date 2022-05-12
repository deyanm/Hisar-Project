package com.example.mig.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mig.model.Poi;
import com.example.mig.repository.Repository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class SettingsViewModel extends ViewModel {
    private static final String TAG = SettingsViewModel.class.getSimpleName();

    private Repository repository;
    private MutableLiveData<List<Poi>> poisList = new MutableLiveData<>();

    @Inject
    public SettingsViewModel(Repository repository) {
        this.repository = repository;
    }

    public MutableLiveData<List<Poi>> getPois() {
        return poisList;
    }

    public void setIsPlaceFav(int id, boolean isFav) {
        repository.setIsPlaceFav(id, isFav);
    }

    public boolean getIsPlaceFav(int id) {
        return repository.getIsPlaceFav(id);
    }

    public void setLangLocale(String locale) {
        repository.setLanguageLocale(locale);
    }

    public String getLangLocale() {
        return repository.getLanguageLocale();
    }

    public void getAllPlacePois(String langCode) {
        repository.getCurrentPlace(langCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(place -> {
                    List<Poi> poisList = new ArrayList<>();
                    poisList.addAll(place.getPois().getSights());
                    poisList.addAll(place.getPois().getHotels());
                    poisList.addAll(place.getPois().getRestaurants());
                    return poisList;
                })
                .subscribe(pois -> poisList.setValue(pois), error -> Log.d(TAG, "error getting place " + error.getMessage()));
    }

}