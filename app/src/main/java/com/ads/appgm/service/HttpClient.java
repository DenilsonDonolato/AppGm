package com.ads.appgm.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.ads.appgm.BuildConfig;
import com.ads.appgm.util.Constants;
import com.ads.appgm.util.SharedPreferenceUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class HttpClient {

    private static BackEndService instance;

    public final static String BASE_URL = BuildConfig.BASE_URL;

    public static BackEndService getInstance(Context context) {
        return instance == null ? initialize(context) : instance;
    }

    public static BackEndService initialize(Context context) {
        instance = buildHttpClient(context);
        return instance;
    }

    private static BackEndService buildHttpClient(Context context) {

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(chain -> {
                    Response response = chain.proceed(chain.request());
                    if (response.code() == 401) {
                        Toast.makeText(context, "Medida Protetiva vencida", Toast.LENGTH_LONG).show();
                        SharedPreferenceUtil.initialize(context);
                        SharedPreferences sp = SharedPreferenceUtil.getSharedPreferences();
                        sp.edit().remove(Constants.USER_TOKEN)
                                .remove(Constants.USER_ID)
                                .remove(Constants.MEASURE_ID)
                                .apply();
                    }
                    return response;
                })
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
