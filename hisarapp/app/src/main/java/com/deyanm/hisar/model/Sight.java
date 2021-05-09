package com.deyanm.hisar.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Sight extends Poi implements Serializable {

    @SerializedName("links")
    @Expose
    private String links;
    @SerializedName("images")
    @Expose
    private List<String> images;

    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

}
