package com.example.mig.utils;

public class DataWrapper<T> {
    private final T data;
    private final String error;

    public DataWrapper(T data, String error) {
        this.data = data;
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }
}