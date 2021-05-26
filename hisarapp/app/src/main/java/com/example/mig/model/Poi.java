package com.example.mig.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Poi implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("placeId")
    @Expose
    private Integer placeId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("short_description")
    @Expose
    private String shortDescription;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("links")
    @Expose
    private String links;
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
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("options")
    @Expose
    private Options options;
    @SerializedName("images")
    @Expose
    private List<String> images;

    private boolean isFav;

    private float distanceKm;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Integer placeId) {
        this.placeId = placeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    public float getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(float distanceKm) {
        this.distanceKm = distanceKm;
    }
}