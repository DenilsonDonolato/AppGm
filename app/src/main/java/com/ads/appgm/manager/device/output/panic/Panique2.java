package com.ads.appgm.manager.device.output.panic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.ads.appgm.notification.Notification;
import com.ads.appgm.util.Constants;
import com.ads.appgm.util.SharedPreferenceUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class Panique2 extends Panique {

    private Context context;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    public Panique2(Context context) {
        super(context);
        this.context = context;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @Override
    protected void turnOn() {
        new SharedPreferenceUtil(context);
        SharedPreferences sp = SharedPreferenceUtil.getSharedePreferences();
        sp.edit().putBoolean(Constants.PANIC, true).apply();
        panic();
        this.updateStatus(true);
    }

    private void panic() {
        LocationManager lm = (LocationManager) context.getSystemService(Activity.LOCATION_SERVICE);
        if (lm == null || !lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Notification notification = new Notification(context);
            notification.show("GPS Desligado", "Ligar o GPS para o funcionamento correto do App", 1);
            return;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //TODO ask permission?
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) {
                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setExpirationDuration(5000);
                locationRequest.setInterval(100);
                locationRequest.setNumUpdates(1);
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                return;
            }
            update(context, location);
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    update(context, location);
                }
            }
        };
    }

    private void update(Context context, Location location) {
        Toast.makeText(context, "Botão do Pânico acionado, latitude: " + location.getLatitude() +
                ", longitude: " + location.getLongitude(), Toast.LENGTH_LONG).show();
        Notification notification = new Notification(context);
        notification.show("Notificação 1", "Latitude: " + location.getLatitude() + "\n" +
                "Longitude: " + location.getLongitude(), 1);
    }

    @Override
    protected void turnOff() {
        new SharedPreferenceUtil(context);
        SharedPreferences sp = SharedPreferenceUtil.getSharedePreferences();
        sp.edit().putBoolean(Constants.PANIC, false).apply();
        this.updateStatus(false);
    }
}