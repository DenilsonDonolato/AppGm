package com.ads.appgm.manager.device.output.panic;

import android.content.Context;

import com.ads.appgm.manager.device.output.OutputDevice;
import com.ads.appgm.util.Constants;

public abstract class Panic extends OutputDevice {
    public static final String TYPE = Constants.ID_DEVICE_OUTPUT_PANIC;

    public Panic(Context context) {
        super(context);
        this.deviceType = TYPE;
    }
}
