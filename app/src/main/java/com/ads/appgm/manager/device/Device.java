package com.ads.appgm.manager.device;

import android.content.Context;

import com.ads.appgm.util.Constants;

public abstract class Device {
    public static final String TYPE = Constants.ID_DEVICE;

    protected final Context mContext;
    protected String deviceType;
    protected boolean isEnabled;

    public Device(Context context) {
        this.mContext = context;
        this.deviceType = TYPE;
        isEnabled = false;
    }

    public final String getDeviceType() {
        return this.deviceType;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
