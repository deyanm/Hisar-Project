package com.example.mig.model;

public class SliderModal {

    // string variable for storing
    // title, image url and description.
    private String title;
    private String heading;

    // creating getter methods.
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeading() {
        return heading;
    }

    // creating setter methods
    public void setHeading(String heading) {
        this.heading = heading;
    }

    // constructor for our modal class
    public SliderModal(String title, String heading) {
        this.title = title;
        this.heading = heading;
    }
}