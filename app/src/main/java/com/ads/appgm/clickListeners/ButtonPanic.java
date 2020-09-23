package com.ads.appgm.clickListeners;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ads.appgm.R;
import com.ads.appgm.notification.Notification;
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

public class ButtonPanic implements View.OnClickListener {

    private final Activity activity;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    public ButtonPanic(Activity activity) {
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {

        SharedPreferences sp = SharedPreferenceUtil.getSharedePreferences();
        boolean isActive = sp.getBoolean("panicActive", false);
        if (isActive) {
            sp.edit().putBoolean("panicActive", false).apply();
            v.setBackground(ContextCompat.getDrawable(activity.getBaseContext(), R.drawable.custom_button_inactive));
        } else {
            sp.edit().putBoolean("panicActive", true).apply();
            v.setBackground(ContextCompat.getDrawable(activity.getBaseContext(), R.drawable.custom_button_active));
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
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                    return;
                }
                update(v, location);
            });

        }

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    activity.runOnUiThread(() -> {
                        update(v, location);
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
        BackEndService client = HttpClient.getInstance();
        List<Double> position = new ArrayList<>();
        position.add(location.getLatitude());
        position.add(location.getLongitude());
        com.ads.appgm.model.Location location1 = new com.ads.appgm.model.Location(position);
        SharedPreferences sp = SharedPreferenceUtil.getSharedePreferences();
        Call<Void> call = client.postLocation(location1, sp.getString(Constants.USER_TOKEN, ""));
        call.enqueue(responseCallback);
    }

    Callback<Void> responseCallback = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (response.code() == 200) {
                Toast.makeText(activity.getBaseContext(), "Enviou GPS", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(activity.getBaseContext(), "Erro " + response.code(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            Toast.makeText(activity.getBaseContext(), "Erro " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            if (!call.isCanceled()) {
                call.cancel();
            }
        }
    };
}
