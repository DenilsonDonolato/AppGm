package com.ads.appgm.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtil {
    private static SharedPreferences mSharedePreferences;

    public SharedPreferenceUtil(Context context) {
        SharedPreferenceUtil.mSharedePreferences = context.getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES,Context.MODE_PRIVATE);
    }

    public static SharedPreferences getSharedePreferences() {
        return mSharedePreferences;
    }

}
