package com.example.mig.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.mig.repository.Repository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AboutPoiViewModel extends ViewModel {
    private static final String TAG = AboutPoiViewModel.class.getSimpleName();

    private Repository repository;

    @Inject
    public AboutPoiViewModel(Repository repository) {
        this.repository = repository;
    }

    public void setIsPlaceFav(int id, boolean isFav) {
        repository.setIsPlaceFav(id, isFav);
    }

    public boolean getIsPlaceFav(int id) {
        return repository.getIsPlaceFav(id);
    }

    public String getLangLocale() {
        return repository.getLanguageLocale();
    }

}