package com.ads.appgm.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class Expired {
    public static boolean checkExpired(Context context) {
        Calendar now = Calendar.getInstance();

        SharedPreferenceUtil.initialize(context);
        long offset = now.get(Calendar.ZONE_OFFSET) +
                now.get(Calendar.DST_OFFSET);
        SharedPreferences sp = SharedPreferenceUtil.getSharedPreferences();
        long expiration = sp.getLong(Constants.EXPIRATION_DATE, Long.MAX_VALUE);
        long test = now.getTimeInMillis() + offset;
        boolean result = test > expiration;
        if (result) {
            if (sp.getLong(Constants.USER_ID, -1L) > 0) {
                NotificationManagerCompat nmc = NotificationManagerCompat.from(context);
                nmc.notify(Constants.NOTIFICATION_ID_EXPIRATION, MyNotification.getInstance(context)
                        .expiration(context));
            }
            sp.edit().remove(Constants.USER_TOKEN)
                    .remove(Constants.USER_ID)
                    .remove(Constants.MEASURE_ID)
                    .apply();
        }
        return result;
    }
}
