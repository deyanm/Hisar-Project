package com.deyanm.hisar.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HisarResponse {

    @SerializedName("bg")
    @Expose
    private Bg bg;
    @SerializedName("ro")
    @Expose
    private Ro ro;
    @SerializedName("en")
    @Expose
    private En en;

    public Bg getBg() {
        return bg;
    }

    public void setBg(Bg bg) {
        this.bg = bg;
    }

    public Ro getRo() {
        return ro;
    }

    public void setRo(Ro ro) {
        this.ro = ro;
    }

    public En getEn() {
        return en;
    }

    public void setEn(En en) {
        this.en = en;
    }

}