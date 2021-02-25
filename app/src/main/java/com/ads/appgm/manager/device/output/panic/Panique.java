package com.ads.appgm.manager.device.output.panic;

import android.content.Context;

import com.ads.appgm.util.Constants;

public abstract class Panique extends Panic {
    public static final String TYPE = Constants.ID_DEVICE_OUTPUT_PANIC_FLASH;

    public Panique(Context context) {
        super(context);
        this.deviceType = TYPE;
    }
}
