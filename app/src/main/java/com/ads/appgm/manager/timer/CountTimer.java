package com.ads.appgm.manager.timer;

import android.os.CountDownTimer;

public class CountTimer extends CountDownTimer {
    private CountTimerListener mListener;
    private String id;

    public CountTimer(String id, double seconds, CountTimerListener listener) {
        super((long) (seconds * 1000), (long) (seconds * 1000));
        this.id = id;
        this.mListener = listener;
    }

    @Override
    public void onTick(long millisUntilFinished) {

    }

    @Override
    public void onFinish() {
        if (this.mListener != null) {
            this.mListener.onCountEnd(this.id);
        }
    }

}
