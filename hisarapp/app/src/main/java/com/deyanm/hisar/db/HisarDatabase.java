package com.deyanm.hisar.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.deyanm.hisar.model.Place;

@Database(entities = {Place.class}, version = 1, exportSchema = false)
public abstract class HisarDatabase extends RoomDatabase {

    public abstract HisarDao hisarDao();

}
