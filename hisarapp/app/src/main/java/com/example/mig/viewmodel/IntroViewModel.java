package com.example.mig.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.mig.repository.Repository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class IntroViewModel extends ViewModel {
    private static final String TAG = IntroViewModel.class.getSimpleName();

    private final Repository repository;

    @Inject
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