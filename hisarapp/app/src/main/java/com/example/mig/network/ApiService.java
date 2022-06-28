package com.example.mig.network;

import com.example.mig.model.HisarResponse;
import com.example.mig.model.LocationResponse;

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

    @GET("db.json")
    @Streaming
    Observable<Response<ResponseBody>> downloadFile();

    @POST("login")
    Observable<HisarResponse> postLogin(@Body HashMap<String, String> body);

    @GET("version.json")
    Observable<ResponseBody> getVersions();

    @GET("place")
    Observable<HisarResponse> getPlace(@Query("id") String id);

    @GET
    Observable<LocationResponse> getCurrentWeather(@Url String url);
}