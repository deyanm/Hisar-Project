package com.deyanm.hisar.model;

import java.util.ArrayList;

public class PlaceResponse {
    private ArrayList<Place> results;

    public PlaceResponse(Integer count, String next, String previous, ArrayList<Place> results) {
        this.results = results;
    }

    public ArrayList<Place> getResults() {
        return results;
    }

    public void setResults(ArrayList<Place> results) {
        this.results = results;
    }
}