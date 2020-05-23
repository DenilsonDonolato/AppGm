package com.ads.appgm.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.media.VolumeProviderCompat;

import com.ads.appgm.R;
import com.ads.appgm.manager.device.input.event.VolumeKeyEvent;
import com.ads.appgm.manager.device.input.key.volume.nativve.VolumeKeyNative;
import com.ads.appgm.manager.device.input.key.volume.rocker.VolumeKeyRocker;
import com.ads.appgm.manager.timer.CountTimer;
import com.ads.appgm.manager.timer.CountTimerListener;
import com.ads.appgm.manager.wakelock.WakeLock;
import com.ads.appgm.util.SettingsUtils;

public class PaniqueManager implements DeviceManagerListener, CountTimerListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static PaniqueManager mInstance;
    private final Context mContext;
    private PaniqueManagerListener mListener;
    private boolean togglePanicIssued = false;
    private ScreenState currentScreenState = ScreenState.SCREEN_ON;
    private CountTimer wakeLockTimer;
    private VolumeProviderCompat mVolumeChangeProvider;

    private PaniqueManager(Context context) {
        super();
        this.mContext = context;
        wakeLockTimer = null;

        DeviceManager.getInstance(this.mContext).setListener(this);

        DeviceManager.getInstance(this.mContext).setPanicType(SettingsUtils.getPanicSource(this.mContext));
        DeviceManager.getInstance(this.mContext).setPanicTimeout(SettingsUtils.getPanicTimeout(this.mContext));

        PreferenceManager.getDefaultSharedPreferences(this.mContext).registerOnSharedPreferenceChangeListener(this);
    }

    public static PaniqueManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PaniqueManager(context);
        }
        return mInstance;
    }

    private static boolean isLegacyDevice() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    public void destroy() {
        PreferenceManager.getDefaultSharedPreferences(this.mContext).unregisterOnSharedPreferenceChangeListener(this);
        mInstance = null;
    }

    public void setListener(PaniqueManagerListener listener) {
        this.mListener = listener;
    }

    public boolean setVolumeKeyEvent(VolumeKeyEvent keyEvent) {
        return DeviceManager.getInstance(this.mContext).setVolumeKeyEvent(keyEvent);
    }

    public void setVolumeValue(int volumeDirection) {
        DeviceManager.getInstance(this.mContext).setVolumeKeyEvent(new VolumeKeyEvent(volumeDirection));
    }

    private void setTimeout(int timeoutSec) {
        if (timeoutSec > 0) {
            if (this.wakeLockTimer == null) {
                this.wakeLockTimer = new CountTimer(WakeLock.TYPE, timeoutSec, this);
            } else {
                this.wakeLockTimer.cancel();
            }
            this.wakeLockTimer.start();
        }
    }

    public void setVolumeProvider(VolumeProviderCompat volumeProvider) {
        this.mVolumeChangeProvider = volumeProvider;
    }

    private void releaseCounter() {
        if (this.wakeLockTimer != null) {
            this.wakeLockTimer.cancel();
            this.wakeLockTimer = null;
        }
    }

    public void togglePanic() {
        if (SettingsUtils.isProximityEnabled(this.mContext)) {
            DeviceManager.getInstance(this.mContext).getProximityValue();
            this.togglePanicIssued = true;
        } else {
            DeviceManager.getInstance(this.mContext).toggleTorch();
        }
    }

    public boolean getPanicStatus() {
        return DeviceManager.getInstance(this.mContext).getPanicStatus();
    }

    public void setScreenEvent(ScreenState state) {
        this.currentScreenState = state;
        switch (state) {
            case SCREEN_OFF:
                this.onScreenOff();
                break;
            case SCREEN_LOCK:
                this.onScreenLock();
                break;
            case SCREEN_ON:
                this.onScreenOn();
                break;
        }
    }

    private void onScreenOff() {
        DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceType(VolumeKeyRocker.TYPE);
        if (SettingsUtils.isScreenOffEnabled(this.mContext)) {
            WakeLockManager.getInstance().acquire(this.mContext, this.mVolumeChangeProvider);
            this.setTimeout(SettingsUtils.getScreenOffTimeoutSec(this.mContext));
            DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceEnabled(true);
        } else {
            if (this.getPanicStatus()) {
                WakeLockManager.getInstance().acquire(this.mContext, this.mVolumeChangeProvider);
                WakeLockManager.getInstance().setHeldOnDemand();
                DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceEnabled(true);
            } else {
                WakeLockManager.getInstance().release();
                this.releaseCounter();
                DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceEnabled(false);
            }
        }
    }

    private void onScreenLock() {
        DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceEnabled(SettingsUtils.isScreenLockEnabled(this.mContext));
        WakeLockManager.getInstance().release();
        if (isLegacyDevice()) {
            DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceType(VolumeKeyRocker.TYPE);
            if (SettingsUtils.isScreenLockEnabled(this.mContext)) {
                WakeLockManager.getInstance().acquire(this.mContext, this.mVolumeChangeProvider);
            }
        } else {
            DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceType(VolumeKeyNative.TYPE);
        }
    }

    private void onScreenOn() {
        DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceEnabled(SettingsUtils.isScreenOnEnabled(this.mContext));
        WakeLockManager.getInstance().release();
        this.releaseCounter();
        if (isLegacyDevice()) {
            DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceType(VolumeKeyRocker.TYPE);
        } else {
            DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceType(VolumeKeyNative.TYPE);
        }
    }

    @Override
    public void onKeyActionPerformed() {
        boolean screenOffFlag = (currentScreenState == ScreenState.SCREEN_OFF) && SettingsUtils.isScreenOffEnabled(this.mContext);
        boolean screenLockFlag = (currentScreenState == ScreenState.SCREEN_LOCK) && SettingsUtils.isScreenLockEnabled(this.mContext);
        boolean screenOnfFlag = (currentScreenState == ScreenState.SCREEN_ON) && SettingsUtils.isScreenOnEnabled(this.mContext);
        if (this.getPanicStatus() || screenOffFlag || screenLockFlag || screenOnfFlag) {
            this.togglePanic();
        }
    }

    @Override
    public void onProximityChanged(boolean status) {
        if (this.togglePanicIssued) {
            if (status) {
                DeviceManager.getInstance(this.mContext).toggleTorch();
            } else {
                this.mListener.onError(this.mContext.getResources().getString(R.string.proximity_error));
            }
            this.togglePanicIssued = false;
        }
    }

    @Override
    public void onPanicStatusChanged(boolean status) {
        if (SettingsUtils.isVibrateEnabled(this.mContext)) {
            DeviceManager.getInstance(this.mContext).vibrate();
        }
        if (WakeLockManager.getInstance().isHeldOnDemand() && !status) {
            WakeLockManager.getInstance().release();
            this.releaseCounter();
            DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceEnabled(false);
        }
        if (this.mListener != null) {
            this.mListener.onPanicStatusChanged(status);
        }
    }

    @Override
    public void onError(String error) {
        if (this.mListener != null) {
            this.mListener.onError(error);
        }
    }

    @Override
    public void onCountEnd(String id) {
        if (id.equals(WakeLock.TYPE)) {
            if (this.getPanicStatus()) {
                WakeLockManager.getInstance().setHeldOnDemand();
                DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceEnabled(true);
            } else {
                WakeLockManager.getInstance().release();
                this.releaseCounter();
                DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceEnabled(false);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case SettingsUtils.PREF_PANIC_SOURCE:
                DeviceManager.getInstance(this.mContext).setPanicType(SettingsUtils.getPanicSource(this.mContext));
                break;
            case SettingsUtils.PREF_PANIC_TIMEOUT:
                DeviceManager.getInstance(this.mContext).setPanicTimeout(SettingsUtils.getPanicTimeout(this.mContext));
                break;
        }
    }
}
