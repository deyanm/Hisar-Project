package com.deyanm.hisar.di;

import android.app.Application;

import com.deyanm.hisar.model.HisarResponse;
import com.deyanm.hisar.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;

@Module
@InstallIn(ApplicationComponent.class)
public class FileModule {

    @Provides
    @Singleton
    public static HisarResponse provideHisarResponse(Application application) {
        String yourFilePath = application.getFilesDir() + "/" + Constants.jsonFileName;
        try {
            BufferedReader br = new BufferedReader(new FileReader(yourFilePath));
            Type type = new TypeToken<HisarResponse>() {
            }.getType();
            return new Gson().fromJson(br, type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new HisarResponse();
    }
}