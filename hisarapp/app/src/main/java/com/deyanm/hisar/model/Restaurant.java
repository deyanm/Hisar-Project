package com.deyanm.hisar.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Restaurant extends Poi {

    @SerializedName("popularity")
    @Expose
    private Integer popularity;
    @SerializedName("stars")
    @Expose
    private Integer stars;
    @SerializedName("options")
    @Expose
    private Options options;
    @SerializedName("rating")
    @Expose
    private Integer rating;

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

}