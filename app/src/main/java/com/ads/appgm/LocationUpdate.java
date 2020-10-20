package com.ads.appgm;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import com.ads.appgm.service.BackEndService;
import com.ads.appgm.service.HttpClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

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

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
