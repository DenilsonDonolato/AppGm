package com.ads.appgm;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ads.appgm.service.BackEndService;
import com.ads.appgm.service.HttpClient;
import com.ads.appgm.util.Constants;
import com.ads.appgm.util.SharedPreferenceUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class LocationUpdate extends Worker {

    private static final String TAG = "com.ads.appgm.NotPanicUpdates";
    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    private CancellationTokenSource cancellationToken;

    public LocationUpdate(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static void iniciarTemporizador(Context context) {

        WorkManager wm = WorkManager.getInstance(context);
        PeriodicWorkRequest pwr = new PeriodicWorkRequest
                .Builder(LocationUpdate.class,
                1, TimeUnit.HOURS,
                15, TimeUnit.MINUTES)
                .build();
        wm.enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP, pwr);
    }

    @NonNull
    @Override
    public Result doWork() {

        SharedPreferenceUtil.initialize(getApplicationContext());
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        Log.e("JOB", "Tentativas: " + this.getRunAttemptCount());
        cancellationToken = new CancellationTokenSource();
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
        Location location = task.getResult();
        Result result = Result.retry();
        if (location != null) {
            BackEndService backEndService = HttpClient.getInstance();
            Log.e("BackEnd", "sending location to backend");
            List<Double> position = new ArrayList<>();
            position.add(location.getLatitude());
            position.add(location.getLongitude());
            com.ads.appgm.model.Location location1 = new com.ads.appgm.model.Location(position, false);
            SharedPreferences sp = SharedPreferenceUtil.getSharedePreferences();
            Call<Void> call = backEndService.postLocation(location1, sp.getString(Constants.USER_TOKEN, ""));
            try {
                Response<Void> response = call.execute();
                if (response.code() == 200) {
                    mMainThreadHandler.post(() -> {
                        Toast.makeText(getApplicationContext(), "Enviou GPS", Toast.LENGTH_LONG).show();
                    });
                    Log.e("JOB", "Sent location");
                    result = Result.success();
                } else {
                    mMainThreadHandler.post(() -> {
                        Toast.makeText(getApplicationContext(), "Erro " + response.code(), Toast.LENGTH_LONG).show();
                    });
                    Log.e("JOB", "not 200");
                    result = Result.retry();
                }
            } catch (IOException e) {
                Log.e("JOB", "Error");
                result = Result.retry();
            }
        }
        return result;
    }

    @Override
    public void onStopped() {
        Log.e("JOB", "Location Updates Cancelado");
        if (cancellationToken != null) {
            cancellationToken.cancel();
        }
        super.onStopped();
    }
}
