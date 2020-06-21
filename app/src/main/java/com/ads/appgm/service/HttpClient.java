package com.ads.appgm.service;

import android.content.SharedPreferences;

import com.ads.appgm.BuildConfig;
import com.ads.appgm.util.Constants;
import com.ads.appgm.util.SharedPreferenceUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpClient {

    private static OkHttpClient instance;

    public final static String BASE_URL = BuildConfig.BASE_URL;
    public final static String LOGIN_URL = BASE_URL + "/login";
    public final static String APPLICATION_JSON = "application/json; chaset=utf-8";

    public static OkHttpClient getInstance() {
        return instance == null ? initialize() : instance;
    }

    public static OkHttpClient initialize() {
        instance = buildHttpClient();
        return instance;
    }

    public static OkHttpClient getLoginClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    private static OkHttpClient buildHttpClient() {
        SharedPreferences sp = SharedPreferenceUtil.getSharedePreferences();
        String token = sp.getString(Constants.USER_TOKEN,"no_token");

        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request request = chain.request();

                    request = request.newBuilder()
                            .header("Authorization", token)
                            .build();

                    Response response = chain.proceed(request);

                    if (response.code() == 401) {
                        //avisa que medidade expirou e sai do App
                    }
                    return response;
                })
                .build();
    }


}
