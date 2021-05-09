package com.deyanm.hisar.viewmodel;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.deyanm.hisar.repository.Repository;

public class SettingsViewModel extends ViewModel {
    private static final String TAG = SettingsViewModel.class.getSimpleName();

    private Repository repository;

    private MutableLiveData<CharSequence> title = new MutableLiveData<>();

    public MutableLiveData<CharSequence> getTitle() {
        return title;
    }

    @ViewModelInject
    public SettingsViewModel(Repository repository) {
        this.repository = repository;
    }

    public void updateActionBarTitle(String title) {
        getTitle().setValue(title);
    }

    public void setLanguageLocale(String locale) {
        repository.setLanguageLocale(locale);
    }

    public String getLanguageLocale() {
        return repository.getLanguageLocale();
    }
}