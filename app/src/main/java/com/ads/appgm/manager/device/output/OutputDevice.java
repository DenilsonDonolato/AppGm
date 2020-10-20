package com.ads.appgm.manager.device.output;

import android.content.Context;

import com.ads.appgm.manager.device.Device;
import com.ads.appgm.util.Constants;

public abstract class OutputDevice extends Device {

    private boolean mStatus;
    private OutputDeviceListener mListener;

    public OutputDevice(Context context) {
        super(context);
        this.mStatus = false;
    }

    protected abstract void turnOn();

    protected abstract void turnOff();

    public final void start(boolean status) {
        if (this.isEnabled) {
            if (status && !this.mStatus)
                this.turnOn();
            else if (!status && this.mStatus)
                this.turnOff();
        }
    }

    public final void toggle() {
        this.start(!this.mStatus);
    }

    public final boolean getStatus() {
        return this.mStatus;
    }

    public final void setListener(OutputDeviceListener listener) {
        this.mListener = listener;
    }

    protected final void updateStatus(boolean status) {
        this.mStatus = status;
        if (this.mListener != null) {
            this.mListener.onStatusChanged(this.deviceType, status);
        }
    }

    protected final void updateError(String error) {
        if (this.mListener != null) {
            this.mListener.onError(error);
        }
    }

}
