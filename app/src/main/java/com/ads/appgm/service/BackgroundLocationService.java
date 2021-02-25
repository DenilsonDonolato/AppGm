package com.ads.appgm.service;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ads.appgm.model.MyLocation;
import com.ads.appgm.util.Constants;
import com.ads.appgm.util.MyNotification;
import com.ads.appgm.util.SharedPreferenceUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class BackgroundLocationService extends Worker {

    private static final String TAG = "com.ads.appgm.NotPanicUpdates";
    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    private CancellationTokenSource cancellationToken;

    public BackgroundLocationService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static void iniciarTemporizador(Context context) {

        WorkManager wm = WorkManager.getInstance(context);
        PeriodicWorkRequest pwr = new PeriodicWorkRequest
                .Builder(BackgroundLocationService.class,
                1, TimeUnit.HOURS,
                15, TimeUnit.MINUTES)
                .build();
        wm.enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP, pwr);
    }

    @NonNull
    @Override
    public Result doWork() {
        MyNotification myNotification = MyNotification.getInstance(getApplicationContext());
        SharedPreferenceUtil.initialize(getApplicationContext());
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        myNotification.createNotificationChannel();
        Log.e("JOB", "Tentativas: " + this.getRunAttemptCount());
        cancellationToken = new CancellationTokenSource();
        boolean failure = false;
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // The PendingIntent to launch activity.
            NotificationManagerCompat nmc = NotificationManagerCompat.from(getApplicationContext());
            nmc.notify(Constants.GPS_PERMISSION_REQUEST, myNotification.openApp(getApplicationContext()));
            failure = true;
        }
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("BackJOB", "Sending LigarGPS");
            myNotification.turnOnGps(getApplicationContext());
            failure = true;
        }
        if (failure) {
            return Result.failure();
        }
        Task<Location> task = client
                .getCurrentLocation(
                        LocationRequest.PRIORITY_HIGH_ACCURACY,
                        cancellationToken.getToken()
                );
        do {
            if (task.isSuccessful()) {
                break;
            }
        } while (!task.isComplete());
        Result result = Result.retry();
        if (task.isCanceled()) {
            return result;
        }
        if (!task.isSuccessful()) {
            Exception e = task.getException();
            if (e != null) {
                FirebaseCrashlytics.getInstance().recordException(task.getException());
            }
            return result;
        }
        Location location = task.getResult();
        if (location != null) {
            BackEndService backEndService = HttpClient.getInstance();
            Log.e("BackEnd", "sending location to backend");
            List<Double> position = new ArrayList<>(2);
            position.add(location.getLatitude());
            position.add(location.getLongitude());
            MyLocation myLocation = new MyLocation(position, false);
            SharedPreferences sp = SharedPreferenceUtil.getSharedPreferences();
            Call<Void> call = backEndService.postLocation(myLocation, sp.getString(Constants.USER_TOKEN, ""));
            try {
                Response<Void> response = call.execute();
                if (response.code() == 200) {
//                    mMainThreadHandler.post(() -> Toast.makeText(getApplicationContext(), "Enviou GPS", Toast.LENGTH_LONG).show());
                    Log.e("JOB", "Sent location");
                    result = Result.success();
                } else {
//                    mMainThreadHandler.post(() -> Toast.makeText(getApplicationContext(), "Erro " + response.code(), Toast.LENGTH_LONG).show());
                    Log.e("JOB", "not 200");
                    result = Result.retry();
                }
            } catch (IOException | RuntimeException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.e("JOB", e.getLocalizedMessage());
                result = Result.retry();
            }
        }
        return result;
    }

    @Override
    public void onStopped() {
        Log.e("JOB", "MyLocation Updates Cancelado");
        if (cancellationToken != null) {
            cancellationToken.cancel();
        }
        super.onStopped();
    }
}
