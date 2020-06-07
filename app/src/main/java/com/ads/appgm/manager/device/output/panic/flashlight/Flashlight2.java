package com.ads.appgm.manager.device.output.panic.flashlight;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.ads.appgm.R;
import com.ads.appgm.notification.Notification;
import com.ads.appgm.util.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

@TargetApi(23)
public class Flashlight2 extends Flashlight {
    public static final String TYPE = Constants.ID_DEVICE_OUTPUT_PANIC_FLASH_NEW;

    private Context context;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    private String[] mCameraIDList;
    private boolean flashSupported;

    public Flashlight2(Context context) {
        super(context);
        flashSupported = false;
        this.deviceType = TYPE;
        this.context = context;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @Override
    protected void turnOn() {
        panic();
        if (!this.getStatus()) {
            CameraManager mCameraManager = (CameraManager) this.mContext.getSystemService(Context.CAMERA_SERVICE);
            try {
                this.mCameraIDList = mCameraManager.getCameraIdList();
            } catch (CameraAccessException e) {
                this.updateError(this.mContext.getResources().getString(R.string.camera_error));
                return;
            }
            try {
                CameraCharacteristics mCameraParameters = mCameraManager.getCameraCharacteristics(this.mCameraIDList[0]);
                this.flashSupported = mCameraParameters.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            } catch (Exception e) {
                this.updateError(this.mContext.getResources().getString(R.string.panic_unsupported));
                return;
            }
            if (this.flashSupported) {
                try {
                    mCameraManager.setTorchMode(this.mCameraIDList[0], true);
                    this.updateStatus(true);
                } catch (CameraAccessException e) {
                    this.updateError(this.mContext.getResources().getString(R.string.camera_busy));
                }
            }
        }
    }

    private void panic() {
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
        panic();
        if (this.getStatus()) {
            if (this.mCameraIDList != null && this.flashSupported) {
                CameraManager mCameraManager = (CameraManager) this.mContext.getSystemService(Context.CAMERA_SERVICE);
                try {
                    mCameraManager.setTorchMode(mCameraIDList[0], false);
                } catch (CameraAccessException e) {
                    this.updateError(this.mContext.getResources().getString(R.string.panic_unsupported));
                    return;
                }
                this.updateStatus(false);
            }
        }
    }

}
