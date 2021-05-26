package com.example.mig.viewmodel;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.ViewModel;

import com.example.mig.repository.Repository;

public class IntroViewModel extends ViewModel {
    private static final String TAG = IntroViewModel.class.getSimpleName();

    private Repository repository;

    @ViewModelInject
    public IntroViewModel(Repository repository) {
        this.repository = repository;
    }

    public String getLangLocale() {
        return repository.getLanguageLocale();
    }

    public boolean isIntroSkipped() {
        return repository.isIntroSkipped();
    }

    public void setIntroSkipped(boolean skipped) {
        repository.setIntroSkipped(skipped);
    }
}