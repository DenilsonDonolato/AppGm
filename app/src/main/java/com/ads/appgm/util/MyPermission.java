package com.ads.appgm.util;

import android.Manifest;
import android.content.Context;
import android.os.Build;

import com.ads.appgm.R;
import com.permissioneverywhere.PermissionEverywhere;
import com.permissioneverywhere.PermissionResultCallback;

public class MyPermission {
    private static MyPermission instance;

    private MyPermission(){}

    public static MyPermission getInstance() {
        return instance == null ? instance = new MyPermission() : instance;
    }

    public void requestGPS(Context context, PermissionResultCallback callback) {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION};
        } else {
            permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION};
        }
        PermissionEverywhere.getPermission(context,
                permissions,
                Constants.GPS_PERMISSION_REQUEST,
                Constants.NOTIFICATION_CHANNEL_NAME,
                "SOS Maria precisa de permissão para usar o GPS",
                R.mipmap.ic_launcher)
                .enqueue(callback);
    }
}
