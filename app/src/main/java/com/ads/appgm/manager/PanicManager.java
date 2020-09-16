package com.ads.appgm.manager;

import android.content.Context;
import android.os.Build;

import com.ads.appgm.manager.device.output.OutputDeviceListener;
import com.ads.appgm.manager.device.output.panic.Panic;
import com.ads.appgm.manager.device.output.panic.flashlight.Flashlight;
import com.ads.appgm.manager.device.output.panic.flashlight.Flashlight1;
import com.ads.appgm.manager.device.output.panic.flashlight.Flashlight2;
import com.ads.appgm.manager.timer.CountTimer;
import com.ads.appgm.manager.timer.CountTimerListener;

public class PanicManager implements CountTimerListener {

    private static PanicManager mInstance;
    private final String flashType;
    private Panic mPanic;
    private OutputDeviceListener mListener;
    private String panicType;
    private boolean isEnabled;
    private CountTimer panicTimer;
    private int panicTimeout;

    private PanicManager(String panicType, boolean enable) {
        this.flashType = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ? Flashlight2.TYPE : Flashlight1.TYPE;
        this.setPanicType(panicType);
        this.isEnabled = enable;

        panicTimer = null;
        panicTimeout = -1;
    }

    public static PanicManager getInstance(String panicType, boolean enable) {
        if (mInstance == null) {
            mInstance = new PanicManager(panicType, enable);
        }
        return mInstance;
    }

    public void setEnabled(boolean enable) {
        if (this.mPanic != null) {
            this.isEnabled = enable;
            this.mPanic.setEnabled(this.isEnabled);
        }
    }

    public String getPanicType() {
        return this.panicType;
    }

    public void setPanicType(String panicType) {
        this.panicType = panicType;
    }

    private void turnOn(Context context) {
        if (this.mPanic == null) {
            if (this.panicType.equals(Flashlight.TYPE)) {
                if (this.flashType.equals(Flashlight1.TYPE)) {
                    this.mPanic = new Flashlight1(context);
                } else if (this.flashType.equals(Flashlight2.TYPE)) {
                    this.mPanic = new Flashlight2(context);
                }
            }
            if (this.mListener != null) {
                this.mPanic.setListener(this.mListener);
            }
        }
        this.mPanic.setEnabled(this.isEnabled);
        this.mPanic.start(true);

        if (this.panicTimer == null) {
            if (this.panicTimeout > 0) {
                this.panicTimer = new CountTimer(Panic.TYPE, this.panicTimeout, this);
                this.panicTimer.start();
            }
        }
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

    public void setTimeout(int timeoutSec) {
        this.panicTimeout = timeoutSec;
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

    @Override
    public void onCountEnd(String id) {
        if (id.equals(Panic.TYPE)) {
            PanicManager.getInstance(Flashlight.TYPE, true).turnOff();
            this.panicTimer = null;
        }
    }
}
