package com.example.mig.viewmodel;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.ViewModel;

import com.example.mig.repository.Repository;

public class AboutPoiViewModel extends ViewModel {
    private static final String TAG = AboutPoiViewModel.class.getSimpleName();

    private Repository repository;

    @ViewModelInject
    public AboutPoiViewModel(Repository repository) {
        this.repository = repository;
    }

    public void setIsPlaceFav(int id, boolean isFav) {
        repository.setIsPlaceFav(id, isFav);
    }

    public boolean getIsPlaceFav(int id) {
        return repository.getIsPlaceFav(id);
    }

}