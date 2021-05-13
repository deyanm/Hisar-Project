package com.example.mig.di;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.mig.utils.Constants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;

@Module
@InstallIn(ApplicationComponent.class)
public class SharedPreferencesModule {

    @Provides
    @Singleton
    public static SharedPreferences provideSharedPrefs(Application application) {
        return application.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
    }
}