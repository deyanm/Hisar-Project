package com.deyanm.hisar.network;

import com.deyanm.hisar.model.Place;
import com.deyanm.hisar.model.PlaceResponse;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {

    @GET
    @Streaming
    Observable<Response<ResponseBody>> downloadFile(@Url String url);

    @POST("login")
    Observable<PlaceResponse> postLogin(@Body HashMap<String, String> body);

    @GET("places")
    Observable<ArrayList<Place>> getPlaces();

    @GET("versions")
    Observable<ResponseBody> getVersions();

    @GET("place")
    Observable<PlaceResponse> getPlace(@Query("id") String id);
}