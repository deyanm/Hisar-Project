package com.deyanm.hisar.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "hotel")
public class Hotel {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;

    private String url;

    public Hotel(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}