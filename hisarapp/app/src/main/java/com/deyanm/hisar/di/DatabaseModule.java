package com.deyanm.hisar.di;

import android.app.Application;

import androidx.room.Room;

import com.deyanm.hisar.db.HisarDao;
import com.deyanm.hisar.db.HisarDatabase;
import com.deyanm.hisar.utils.Constants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;

@Module
@InstallIn(ApplicationComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public static HisarDatabase provideHisarDB(Application application){
        return Room.databaseBuilder(application, HisarDatabase.class, Constants.DataBaseName)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }

    @Provides
    @Singleton
    public static HisarDao providePokeDao(HisarDatabase hisarDB){
        return hisarDB.hisarDao();
    }
}