package com.deyanm.hisar.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Options {

    @SerializedName("available")
    @Expose
    private Integer available;
    @SerializedName("breakfast")
    @Expose
    private Boolean breakfast;
    @SerializedName("lunch")
    @Expose
    private Boolean lunch;
    @SerializedName("dinner")
    @Expose
    private Boolean dinner;

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

    public Boolean getLunch() {
        return lunch;
    }

    public void setLunch(Boolean lunch) {
        this.lunch = lunch;
    }

    public Boolean getDinner() {
        return dinner;
    }

    public void setDinner(Boolean dinner) {
        this.dinner = dinner;
    }

}