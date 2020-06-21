package com.ads.appgm.service;

import android.content.SharedPreferences;

import com.ads.appgm.BuildConfig;
import com.ads.appgm.util.Constants;
import com.ads.appgm.util.SharedPreferenceUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class HttpClient {

    private static BackEndService instance;

    public final static String BASE_URL = BuildConfig.BASE_URL;

    public static BackEndService getInstance() {
        return instance == null ? initialize() : instance;
    }

    public static BackEndService initialize() {
        instance = buildHttpClient();
        return instance;
    }

    private static BackEndService buildHttpClient() {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(BackEndService.class);
    }


}
