package com.ads.appgm;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.ads.appgm.service.BackEndService;
import com.ads.appgm.service.HttpClient;
import com.ads.appgm.util.Constants;
import com.ads.appgm.util.SharedPreferenceUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationUpdate extends JobService {
    public static void iniciarTemporizador(Context context) {
        ComponentName syncActivitiesComponent = new ComponentName(context, LocationUpdate.class);
        JobInfo syncInfo = new JobInfo.Builder(1, syncActivitiesComponent)
                .setPersisted(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(60 * 60 * 1000)
                .build();

        JobScheduler activitySyncJobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int resultActivitySync = activitySyncJobScheduler.schedule(syncInfo);

        if (resultActivitySync == JobScheduler.RESULT_SUCCESS) {
            Log.d("Job", "JobScheduler Check new Tickets funcionando");
        } else {
            Log.d("Job", "jobScheduler Check new Tickets falhou!");
        }
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        LocationCallback  locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Endpoint backend
                    // ...
                    BackEndService client = HttpClient.getInstance();
                    List<Double> position = new ArrayList<>();
                    position.add(location.getLatitude());
                    position.add(location.getLongitude());
                    com.ads.appgm.model.Location location1 = new com.ads.appgm.model.Location(position);
                    SharedPreferences sp = SharedPreferenceUtil.getSharedePreferences();
                    Call<Void> call = client.postLocation(location1, sp.getString(Constants.USER_TOKEN, ""));
                    call.enqueue(responseCallback);
                }
            }
        };
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        client.requestLocationUpdates(locationRequest,
                locationCallback, Looper.getMainLooper());
        return false;
    }
    Callback<Void> responseCallback = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (response.code() == 200) {
                Toast.makeText(getApplicationContext(), "Enviou GPS", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText( getApplicationContext() , "Erro " + response.code(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            Toast.makeText(getApplicationContext(), "Erro " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            if (!call.isCanceled()) {
                call.cancel();
            }
        }
    };
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
