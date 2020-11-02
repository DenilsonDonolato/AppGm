package com.ads.appgm.clickListeners;

import android.content.SharedPreferences;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.ads.appgm.MainActivity;
import com.ads.appgm.R;
import com.ads.appgm.util.Constants;
import com.ads.appgm.util.MyNotification;
import com.ads.appgm.util.SharedPreferenceUtil;

public class ButtonPanic implements View.OnClickListener {

    private final MainActivity activity;

    public ButtonPanic(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        SharedPreferences sp = SharedPreferenceUtil.getSharedePreferences();
        boolean isActive = sp.getBoolean(Constants.PANIC, false);
        if (isActive) {
            MyNotification myNotification = MyNotification.getInstance(activity.getApplicationContext());
            myNotification.cancelAll();
            sp.edit().putBoolean(Constants.PANIC, false).apply();
            v.setBackground(ContextCompat.getDrawable(activity.getBaseContext(), R.drawable.custom_button_inactive));
            activity.getForegroundLocationService().removeLocationUpdates();
        } else {
            activity.getForegroundLocationService().requestLocationUpdates();
            sp.edit().putBoolean(Constants.PANIC, true).apply();
            v.setBackground(ContextCompat.getDrawable(activity.getBaseContext(), R.drawable.custom_button_active));
        }
    }
}
