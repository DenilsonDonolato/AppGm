package com.ads.appgm.manager;

import android.content.Context;

import com.ads.appgm.manager.device.output.OutputDeviceListener;
import com.ads.appgm.manager.device.output.panic.Panic;
import com.ads.appgm.manager.device.output.panic.Panique2;

public class PanicManager {

    private static PanicManager mInstance;
    private Panic mPanic;
    private OutputDeviceListener mListener;
    private boolean isEnabled;

    private PanicManager(boolean enable) {
        this.isEnabled = enable;
    }

    public static PanicManager getInstance(boolean enable) {
        if (mInstance == null) {
            mInstance = new PanicManager(enable);
        }
        return mInstance;
    }

    public void setEnabled(boolean enable) {
        if (this.mPanic != null) {
            this.isEnabled = enable;
            this.mPanic.setEnabled(this.isEnabled);
        }
    }

    private void turnOn(Context context) {
        if (this.mPanic == null) {
            this.mPanic = new Panique2(context);
            if (this.mListener != null) {
                this.mPanic.setListener(this.mListener);
            }
        }
        this.mPanic.setEnabled(this.isEnabled);
        this.mPanic.start(true);
    }

    private void turnOff() {
        if (this.mPanic != null) {
            this.mPanic.start(false);
            this.mPanic = null;
        }
    }

    public void toggle(Context context) {
        if (this.mPanic == null) {
            this.turnOn(context);
        } else {
            if (this.mPanic.getStatus()) {
                this.turnOff();
            } else {
                this.turnOn(context);
            }
        }
    }

    public boolean getStatus() {
        return this.mPanic != null && this.mPanic.getStatus();
    }

    public void setListener(OutputDeviceListener listener) {
        this.mListener = listener;
        if (this.mPanic != null) {
            this.mPanic.setListener(listener);
        }
    }
}
