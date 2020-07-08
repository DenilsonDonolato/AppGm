package com.ads.appgm.service;

import com.ads.appgm.model.Login;
import com.ads.appgm.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BackEndService {

    @POST("mobile/login")
    Call<LoginResponse> loginRequest(@Body Login login);

//    @POST("location")
//    Call<String> postLocation(@Body Location location, @Header("Authorization") String token);
}
