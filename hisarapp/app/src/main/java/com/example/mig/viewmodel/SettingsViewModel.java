package com.example.mig.viewmodel;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.ViewModel;

import com.example.mig.repository.Repository;

public class SettingsViewModel extends ViewModel {
    private static final String TAG = SettingsViewModel.class.getSimpleName();

    private Repository repository;

    @ViewModelInject
    public SettingsViewModel(Repository repository) {
        this.repository = repository;
    }

    public void setLangLocale(String locale) {
        repository.setLanguageLocale(locale);
    }

    public String getLangLocale() {
        return repository.getLanguageLocale();
    }

}