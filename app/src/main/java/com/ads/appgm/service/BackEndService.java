package com.ads.appgm.service;

import com.ads.appgm.model.MyLocation;
import com.ads.appgm.model.Login;
import com.ads.appgm.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface BackEndService {

    @POST("mobile/login")
    Call<LoginResponse> loginRequest(@Body Login login);

    @POST("mobile/localization")
    Call<Void> postLocation(@Body MyLocation myLocation, @Header("Authorization") String token);
}
