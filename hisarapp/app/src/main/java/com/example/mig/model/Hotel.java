package com.example.mig.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Hotel extends Poi {

    @SerializedName("maxPricePerNight")
    @Expose
    private Integer maxPricePerNight;
    @SerializedName("maxPrice")
    @Expose
    private Integer maxPrice;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("popularity")
    @Expose
    private Integer popularity;
    @SerializedName("stars")
    @Expose
    private Integer stars;
    @SerializedName("rating")
    @Expose
    private Integer rating;

    public Integer getMaxPricePerNight() {
        return maxPricePerNight;
    }

    public void setMaxPricePerNight(Integer maxPricePerNight) {
        this.maxPricePerNight = maxPricePerNight;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

}