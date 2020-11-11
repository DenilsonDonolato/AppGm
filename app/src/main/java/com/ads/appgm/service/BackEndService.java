package com.ads.appgm.service;

import com.ads.appgm.model.Actuation;
import com.ads.appgm.model.Login;
import com.ads.appgm.model.LoginResponse;
import com.ads.appgm.model.MyLocation;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BackEndService {

    @POST("mobile/login")
    Call<LoginResponse> loginRequest(@Body Login login);

    @POST("mobile/localization")
    Call<Void> postLocation(@Body MyLocation myLocation, @Header("Authorization") String token);

    @GET("mobile/actuation/{id}")
    Call<Actuation> getActuation(@Header("Authorization") String token, @Path("id") String measureId);
}
