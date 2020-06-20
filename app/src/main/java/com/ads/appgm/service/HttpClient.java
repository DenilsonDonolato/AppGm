package com.ads.appgm.service;

import com.ads.appgm.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpClient {

    private static OkHttpClient instance;

    public final static String BASE_URL = BuildConfig.BASE_URL;
    public final static String APPLICATION_JSON = "application/json; chaset=utf-8";

    public static OkHttpClient getInstance() {
        return instance == null ? initialize() : instance;
    }

    public static OkHttpClient initialize() {
        instance = buildHttpClient();
        return instance;
    }

    private static OkHttpClient buildHttpClient() {
        String token ="";

        return new OkHttpClient.Builder()
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
