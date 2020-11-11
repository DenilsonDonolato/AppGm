/*
 *     Copyright (C) 2016  Merbin J Anselm <merbinjanselm@gmail.com>
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.ads.appgm.util;

/**
 * Created by Merbin J Anselm on 19-Feb-17.
 */

public class Constants {
    public static final String ID_DEVICE = "0";
    public static final int LOGIN_INTENT_REQUEST = 1;
    public static final int GPS_PERMISSION_REQUEST = 2;
    public static final int GPS_TURN_ON = 3;
    public static final int REQUEST_PERMISSION_SETTING = 4;
    public static final String SHARED_PREFERENCES = "com.ads.appgm.appgm_preferences";
    public static final String USER_TOKEN = "API_TOKEN";
    public static final String USER_ID = "USER_ID";
    public static final String USER_NAME = "USER_NAME";
    public static final String MEASURE_ID = "MEASURE_ID";
    public static final String EXPIRATION_DATE = "EXPIRATION_DATE";
    public static final String PANIC = "panicActive";
    public static final int NOTIFICATION_ID_FOREGROUND_SERVICE = 123;
    public static final int NOTIFICATION_ID_LIGAR_GPS = 124;
    public static final String NOTIFICATION_CHANNEL_ID = "SOS_MARIA";
    public static final String NOTIFICATION_CHANNEL_NAME = "SOS Maria";
    public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Notificações de SOS Maria";

    public static final String PACKAGE_NAME = "com.ads.appgm";
    public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
    public static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    public static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";
    public static final String EXTRA_STARTED_FROM_PANICQUICK = PACKAGE_NAME +
            ".started_from_panicquick";
    public static final float SMALLEST_DISPLACEMENT = 10;
    public static final String FIRST_ACTUATION = "FIRST_ACTUATION";

    public final static String ID_DEVICE_OUTPUT = "1";
    public final static String ID_DEVICE_OUTPUT_PANIC = "10";
    public final static String ID_DEVICE_OUTPUT_PANIC_FLASH = "11";
    public final static String ID_DEVICE_OUTPUT_PANIC_FLASH_LEGACY = "12";
    public final static String ID_DEVICE_OUTPUT_PANIC_FLASH_NEW = "13";
    public final static String ID_DEVICE_OUTPUT_PANIC_SCREEN = "14";
    public final static String ID_DEVICE_OUTPUT_VIBRATOR = "15";

    public final static String ID_DEVICE_INPUT = "2";
    public final static String ID_DEVICE_INPUT_VOLUMEKEY = "20";
    public final static String ID_DEVICE_INPUT_VOLUMEKEY_NATIVE = "21";
    public final static String ID_DEVICE_INPUT_VOLUMEKEY_ROCKER = "22";
    public final static String ID_DEVICE_INPUT_PROXIMITY = "23";
}
