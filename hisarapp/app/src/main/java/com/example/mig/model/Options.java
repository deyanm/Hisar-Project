package com.example.mig.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Options implements Serializable {

    @SerializedName("available")
    @Expose
    private Integer available;
    @SerializedName("breakfast")
    @Expose
    private Boolean breakfast;
    @SerializedName("wifi")
    @Expose
    private Boolean wifi;
    @SerializedName("airport")
    @Expose
    private Boolean airport;
    @SerializedName("bar")
    @Expose
    private Boolean bar;
    @SerializedName("gym")
    @Expose
    private Boolean gym;
    @SerializedName("beach")
    @Expose
    private Boolean beach;
    @SerializedName("casino")
    @Expose
    private Boolean casino;
    @SerializedName("elevator")
    @Expose
    private Boolean elevator;
    @SerializedName("laundry")
    @Expose
    private Boolean laundry;
    @SerializedName("meeting")
    @Expose
    private Boolean meeting;
    @SerializedName("pet")
    @Expose
    private Boolean pet;
    @SerializedName("restaurant")
    @Expose
    private Boolean restaurant;
    @SerializedName("wheelchair")
    @Expose
    private Boolean wheelchair;

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

    public Boolean getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(Boolean breakfast) {
        this.breakfast = breakfast;
    }

    public Boolean getWifi() {
        return wifi;
    }

    public void setWifi(Boolean wifi) {
        this.wifi = wifi;
    }

    public Boolean getAirport() {
        return airport;
    }

    public void setAirport(Boolean airport) {
        this.airport = airport;
    }

    public Boolean getBar() {
        return bar;
    }

    public void setBar(Boolean bar) {
        this.bar = bar;
    }

    public Boolean getGym() {
        return gym;
    }

    public void setGym(Boolean gym) {
        this.gym = gym;
    }

    public Boolean getBeach() {
        return beach;
    }

    public void setBeach(Boolean beach) {
        this.beach = beach;
    }

    public Boolean getCasino() {
        return casino;
    }

    public void setCasino(Boolean casino) {
        this.casino = casino;
    }

    public Boolean getElevator() {
        return elevator;
    }

    public void setElevator(Boolean elevator) {
        this.elevator = elevator;
    }

    public Boolean getLaundry() {
        return laundry;
    }

    public void setLaundry(Boolean laundry) {
        this.laundry = laundry;
    }

    public Boolean getMeeting() {
        return meeting;
    }

    public void setMeeting(Boolean meeting) {
        this.meeting = meeting;
    }

    public Boolean getPet() {
        return pet;
    }

    public void setPet(Boolean pet) {
        this.pet = pet;
    }

    public Boolean getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Boolean restaurant) {
        this.restaurant = restaurant;
    }

    public Boolean getWheelchair() {
        return wheelchair;
    }

    public void setWheelchair(Boolean wheelchair) {
        this.wheelchair = wheelchair;
    }


}