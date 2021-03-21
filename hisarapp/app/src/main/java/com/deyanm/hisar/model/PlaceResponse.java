package com.deyanm.hisar.model;

import java.util.ArrayList;

public class PlaceResponse {
    private Integer count;
    private String next, previous;
    private ArrayList<Place> results;

    public PlaceResponse(Integer count, String next, String previous, ArrayList<Place> results) {
        this.count = count;
        this.next = next;
        this.previous = previous;
        this.results = results;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public ArrayList<Place> getResults() {
        return results;
    }

    public void setResults(ArrayList<Place> results) {
        this.results = results;
    }
}