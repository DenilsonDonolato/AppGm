package com.ads.appgm.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ads.appgm.R;

public class SettingsUtils {

    public static final String PREF_PANIC_TIMEOUT = "pref_panic_timeout";
    public static final String PREF_PANIC_SOURCE = "pref_panic_source";
    public static final String PREF_VIBRATE = "pref_vibrate";
    private static final String PREF_FIRST_TIME = "pref_first_time";
    private static final String PREF_SCREEN_ON = "pref_screen_on";
    private static final String PREF_SCREEN_LOCK = "pref_screen_lock";
    private static final String PREF_SCREEN_OFF = "pref_screen_off_timeout";
    private static final String PREF_PROXIMITY = "pref_proximity";

    public static boolean isFirstTime(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(SettingsUtils.PREF_FIRST_TIME, true);
    }

    public static void setFirstTime(final Context context, final boolean newValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(SettingsUtils.PREF_FIRST_TIME, newValue).apply();
    }

    public static boolean isScreenOnEnabled(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(SettingsUtils.PREF_SCREEN_ON, context.getResources().getBoolean(R.bool.pref_default_screen_on));
    }

    public static boolean isScreenLockEnabled(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(SettingsUtils.PREF_SCREEN_LOCK, context.getResources().getBoolean(R.bool.pref_default_screen_lock));
    }

    public static boolean isScreenOffEnabled(final Context context) {
        int value = SettingsUtils.getScreenOffTimeoutSec(context);
        return value != 0;
    }

    public static boolean isScreenOffIndefinite(final Context context) {
        int value = SettingsUtils.getScreenOffTimeoutSec(context);
        return value == -1;
    }

    public static int getScreenOffTimeoutSec(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(sp.getString(SettingsUtils.PREF_SCREEN_OFF, context.getResources().getString(R.string.pref_default_screen_off_timeout)));
    }

    public static boolean isPanicTimeoutIndefinite(final Context context) {
        int value = SettingsUtils.getPanicTimeout(context);
        return value == -1;
    }

    public static int getPanicTimeout(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(sp.getString(SettingsUtils.PREF_PANIC_TIMEOUT, context.getResources().getString(R.string.pref_default_panic_timeout)));
    }

    public static String getPanicSource(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(SettingsUtils.PREF_PANIC_SOURCE, context.getResources().getString(R.string.pref_default_panic_source));
    }

    public static boolean isProximityEnabled(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(SettingsUtils.PREF_PROXIMITY, context.getResources().getBoolean(R.bool.pref_default_proximity));
    }

    public static boolean isVibrateEnabled(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(SettingsUtils.PREF_VIBRATE, context.getResources().getBoolean(R.bool.pref_default_vibrate));
    }
}
