package com.example.mig.di;

import android.app.Application;

import com.example.mig.model.HisarResponse;
import com.example.mig.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class FileModule {

    @Provides
    public static HisarResponse provideHisarResponse(Application application) {
        File file = new File(application.getFilesDir(), Constants.jsonFileName);
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                Type type = new TypeToken<HisarResponse>() {
                }.getType();
                return new Gson().fromJson(br, type);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new HisarResponse();
    }
}