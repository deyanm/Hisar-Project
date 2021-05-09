package com.deyanm.hisar.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Pois {

    @SerializedName("sights")
    @Expose
    private ArrayList<Poi> sights;
    @SerializedName("hotels")
    @Expose
    private ArrayList<Poi> hotels;
    @SerializedName("restaurants")
    @Expose
    private ArrayList<Poi> restaurants;

    public ArrayList<Poi> getSights() {
        return sights;
    }

    public void setSights(ArrayList<Poi> sights) {
        this.sights = sights;
    }

    public ArrayList<Poi> getHotels() {
        return hotels;
    }

    public void setHotels(ArrayList<Poi> hotels) {
        this.hotels = hotels;
    }

    public ArrayList<Poi> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(ArrayList<Poi> restaurants) {
        this.restaurants = restaurants;
    }

}