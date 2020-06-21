package com.ads.appgm.clickListeners;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.ads.appgm.notification.Notification;
import com.ads.appgm.util.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

public class ButtonPanic implements View.OnClickListener {

    private final Activity activity;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    public ButtonPanic(FusedLocationProviderClient fusedLocationProviderClient, Activity activity) {
        this.fusedLocationProviderClient = fusedLocationProviderClient;
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {

        if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.GPS_PERMISSION_REQUEST);
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) {
                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setExpirationDuration(5000);
                locationRequest.setInterval(100);
                locationRequest.setNumUpdates(1);
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,Looper.getMainLooper());
                return;
            }
            update(v, location);
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    activity.runOnUiThread(() -> {
                        update(v,location);
                    });
                }
            }
        };
    }

    private void update(View v, Location location) {
        Toast.makeText(v.getContext(), "Botão do Pânico acionado, latitude: " + location.getLatitude() +
                ", longitude: " + location.getLongitude(), Toast.LENGTH_LONG).show();
        Notification notification = new Notification(v.getContext());
        notification.show("Notificação 1", "Latitude: " + location.getLatitude() + "\n" +
                "Longitude: " + location.getLongitude(), 1);
    }
}
