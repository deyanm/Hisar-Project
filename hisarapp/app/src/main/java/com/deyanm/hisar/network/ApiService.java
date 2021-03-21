package com.deyanm.hisar.network;

import com.deyanm.hisar.model.PlaceResponse;

import java.util.HashMap;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("login")
    Observable<PlaceResponse> postLogin(@Body HashMap<String, String> body);

    @GET("place")
    Observable<PlaceResponse> getPlaces();

    @GET("place")
    Observable<PlaceResponse> getPlace(@Query("id") String id);
}