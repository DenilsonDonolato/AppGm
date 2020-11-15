package com.ads.appgm.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtil {
    private static SharedPreferences mSharedePreferences;

    private SharedPreferenceUtil() {
    }

    public static void initialize(Context context) {
        if( mSharedePreferences == null) {
            SharedPreferenceUtil.mSharedePreferences = context.getApplicationContext()
                    .getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        }
    }

    public static SharedPreferences getSharedPreferences() {
        return mSharedePreferences;
    }

}
