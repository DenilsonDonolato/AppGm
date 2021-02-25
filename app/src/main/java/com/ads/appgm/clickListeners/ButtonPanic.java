package com.ads.appgm.clickListeners;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.ads.appgm.MainActivity;
import com.ads.appgm.R;
import com.ads.appgm.util.Constants;

public class ButtonPanic implements View.OnClickListener {

    private MainActivity activity;

    public ButtonPanic(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        if (activity.gpsLigado()) {
            activity.startForegroundService();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.GPS).setMessage(R.string.reason_gps)
                    .setNegativeButton(R.string.later, listenerGps)
                    .setCancelable(false)
                    .setPositiveButton(R.string.turn_on_gps, listenerGps)
                    .show();
        }
    }

    DialogInterface.OnClickListener listenerGps = (dialog, which) -> {
        if (which == Dialog.BUTTON_POSITIVE) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivityForResult(intent, Constants.GPS_TURN_ON);
        } else {
            dialog.dismiss();
        }
    };
}
