package com.ads.appgm.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyTimestamp {
    private static final String sdf = "dd/MM/yyyy HH:mm:ss";
    private static final String timestamp = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String shortSdt = "dd/MM/yyyy";

    public static String timestamp() {
        return isoFromCalendar(Calendar.getInstance());
    }

    public static String isoFromCalendar(Calendar date) {
        SimpleDateFormat shortSdt = new SimpleDateFormat(MyTimestamp.timestamp, Locale.getDefault());
        long longFormat = date.getTimeInMillis();
        Date dateFormat = new Date(longFormat);
        return shortSdt.format(dateFormat);
    }
}