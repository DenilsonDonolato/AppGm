package com.ads.appgm.manager;

import android.content.Context;
import android.os.Build;

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
            } else if (this.panicType.equals(Screenlight.TYPE)) {
                this.mPanic = new Screenlight(context);
            }
            if (this.mListener != null) {
                this.mPanic.setListener(this.mListener);
            }
        }
        this.mPanic.setEnabled(this.isEnabled);
        this.mPanic.start(true);

        if (this.panicTimer == null) {
            if (this.panicTimeout > 0) {
                this.panicTimer = new CountTimer(Torch.TYPE, this.panicTimeout, this);
                this.panicTimer.start();
            }
        }
    }

}
