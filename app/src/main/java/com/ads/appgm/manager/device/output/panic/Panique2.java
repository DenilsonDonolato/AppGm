package com.ads.appgm.manager.device.output.panic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;

import com.ads.appgm.service.ForegroundLocationService;
import com.ads.appgm.util.Constants;
import com.ads.appgm.util.MyNotification;
import com.ads.appgm.util.SharedPreferenceUtil;
import com.google.android.gms.location.LocationCallback;

public class Panique2 extends Panique {

    private final Context context;
    private LocationCallback locationCallback;

    public Panique2(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void turnOn() {
        SharedPreferenceUtil.initialize(context);
        SharedPreferences sp = SharedPreferenceUtil.getSharedePreferences();
        sp.edit().putBoolean(Constants.PANIC, true).apply();
        panic();
        this.updateStatus(true);
    }

    private void panic() {
        LocationManager lm = (LocationManager) context.getSystemService(Activity.LOCATION_SERVICE);
        if (lm == null || !lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            MyNotification myNotification = MyNotification.getInstance(context);
            myNotification.turnOnGps(context);
            return;
        }
        Intent intent = new Intent(context, ForegroundLocationService.class);
        intent.putExtra(Constants.EXTRA_STARTED_FROM_PANICQUICK, true);
        intent.putExtra(Constants.PANIC, true);
        context.startService(intent);
    }

    @Override
    protected void turnOff() {
        SharedPreferenceUtil.initialize(context);
        SharedPreferences sp = SharedPreferenceUtil.getSharedePreferences();
        sp.edit().putBoolean(Constants.PANIC, false).apply();
        Intent intent = new Intent(context, ForegroundLocationService.class);
        intent.putExtra(Constants.EXTRA_STARTED_FROM_PANICQUICK, true);
        intent.putExtra(Constants.PANIC, false);
        context.startService(intent);
        this.updateStatus(false);
    }
}