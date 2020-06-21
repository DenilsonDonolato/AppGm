package com.ads.appgm.manager.device.output.panic.flashlight;

import android.content.Context;

import com.ads.appgm.manager.device.output.panic.Panic;
import com.ads.appgm.util.Constants;

public abstract class Flashlight extends Panic {
    public static final String TYPE = Constants.ID_DEVICE_OUTPUT_PANIC_FLASH;

    public Flashlight(Context context) {
        super(context);
        this.deviceType = TYPE;
    }
}
