package com.ads.appgm.manager;

import android.content.Context;
import android.util.Log;

import androidx.media.VolumeProviderCompat;

import com.ads.appgm.manager.wakelock.WakeLock;

public class WakeLockManager {
    private static WakeLockManager mInstance;
    private WakeLock wakeLock;
    private boolean isEnabled;
    private boolean isHeldOnDemand;

    private WakeLockManager() {
        this.isEnabled = true;
        this.isHeldOnDemand = false;
    }

    public static WakeLockManager getInstance() {
        if (mInstance == null) {
            mInstance = new WakeLockManager();
        }
        return mInstance;
    }

    public void acquire(Context context, VolumeProviderCompat volumeProvider) {
        if (this.wakeLock == null) {
            this.wakeLock = new WakeLock();
        }
        this.wakeLock.setEnabled(this.isEnabled);
        this.wakeLock.acquire(context, volumeProvider);
        Log.e("Torchie Wakelock", String.valueOf(this.wakeLock.isHeld()));
    }

    public void setHeldOnDemand() {
        this.isHeldOnDemand = true;
    }

    public boolean isHeldOnDemand() {
        return this.isHeldOnDemand;
    }

    public void release() {
        if (this.wakeLock != null) {
            this.wakeLock.release();
            this.isHeldOnDemand = false;
            Log.e("Torchie Wakelock", String.valueOf(this.wakeLock.isHeld()));
        }
        this.wakeLock = null;
    }

    public boolean isHeld() {
        return this.wakeLock != null && this.wakeLock.isHeld();
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        if (this.wakeLock != null) {
            this.wakeLock.setEnabled(this.isEnabled);
        }
    }
}