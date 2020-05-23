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

package com.ads.appgm.manager.device.output.vibrator;

import android.content.Context;

import com.ads.appgm.R;
import com.ads.appgm.manager.device.output.OutputDevice;
import com.ads.appgm.util.Constants;

/**
 * Created by Merbin J Anselm on 05-Feb-17.
 */

public class Vibrator extends OutputDevice {
    public static final String TYPE = Constants.ID_DEVICE_OUTPUT_VIBRATOR;

    public final long duration;

    public Vibrator(Context context) {
        super(context);
        this.deviceType = TYPE;
        this.duration = context.getResources().getInteger(R.integer.pref_default_vibration_duration);
    }

    @Override
    protected void turnOn() {
        final android.os.Vibrator vibrator = (android.os.Vibrator) this.mContext.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(duration);
    }

    @Override
    protected void turnOff() {
        final android.os.Vibrator vibrator = (android.os.Vibrator) this.mContext.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.cancel();
    }

    public void vibrate() {
        this.turnOn();
    }
}
