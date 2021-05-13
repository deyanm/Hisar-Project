package com.example.mig.model;

import androidx.room.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "place")
public class Place {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("info")
    @Expose
    private Info info;
    @SerializedName("mig")
    @Expose
    private Mig mig;
    @SerializedName("pois")
    @Expose
    private Pois pois;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public Mig getMig() {
        return mig;
    }

    public void setMig(Mig mig) {
        this.mig = mig;
    }

    public Pois getPois() {
        return pois;
    }

    public void setPois(Pois pois) {
        this.pois = pois;
    }
}
