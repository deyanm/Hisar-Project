package com.deyanm.hisar.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.deyanm.hisar.model.Place;

import java.util.List;

@Dao
public interface HisarDao {

    @Insert
    void insertPlace(Place pokemon);

    @Query("DELETE FROM place WHERE id = :id")
    void deletePlace(int id);

    @Query("DELETE FROM place")
    void deleteAll();

    @Query("SELECT * FROM place")
    LiveData<List<Place>> getPlaces();
}
