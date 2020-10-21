package com.ads.appgm;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.ads.appgm.service.BackEndService;
import com.ads.appgm.service.HttpClient;
import com.ads.appgm.util.Constants;
import com.ads.appgm.util.SharedPreferenceUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationUpdate extends JobService {

    private CancellationTokenSource cancellationTokenSource;
    private JobParameters jobParameters;

    public static void iniciarTemporizador(Context context) {

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler.getAllPendingJobs().size()>0) {
            return;
        }

        ComponentName syncActivitiesComponent = new ComponentName(context, LocationUpdate.class);
        JobInfo syncInfo = new JobInfo.Builder(1, syncActivitiesComponent)
                .setPersisted(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(60 * 1000)
                .build();

        int resultActivitySync = jobScheduler.schedule(syncInfo);

        if (resultActivitySync == JobScheduler.RESULT_SUCCESS) {
            Log.d("Job", "Location on Background started");
        } else {
            Log.d("Job", "Location on Background started");
        }
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        SharedPreferenceUtil.initialize(this);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        this.jobParameters = jobParameters;
        this.cancellationTokenSource = new CancellationTokenSource();

        client.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, cancellationTokenSource.getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        BackEndService backEndService = HttpClient.getInstance();
                        Log.i("BackEnd", "sending location to backend");
                        List<Double> position = new ArrayList<>();
                        position.add(location.getLatitude());
                        position.add(location.getLongitude());
                        com.ads.appgm.model.Location location1 = new com.ads.appgm.model.Location(position, false);
                        SharedPreferences sp = SharedPreferenceUtil.getSharedePreferences();
                        Call<Void> call = backEndService.postLocation(location1, sp.getString(Constants.USER_TOKEN, ""));
                        call.enqueue(responseCallback);
                    }
                });
        return true;
    }

    Callback<Void> responseCallback = new Callback<Void>() {
        @Override
        public void onResponse(@NotNull Call<Void> call, Response<Void> response) {
            if (response.code() == 200) {
                Toast.makeText(getApplicationContext(), "Enviou GPS", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText( getApplicationContext() , "Erro " + response.code(), Toast.LENGTH_LONG).show();
            }
            jobFinished(jobParameters,false);
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            Toast.makeText(getApplicationContext(), "Erro " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            if (!call.isCanceled()) {
                call.cancel();
            }
            jobFinished(jobParameters,true);
        }
    };

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        cancellationTokenSource.cancel();
        return true;
    }
}
