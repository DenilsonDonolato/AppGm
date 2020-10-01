package com.ads.appgm.manager;

import android.content.Context;
import android.preference.PreferenceManager;

import androidx.media.VolumeProviderCompat;

import com.ads.appgm.R;
import com.ads.appgm.manager.device.input.event.VolumeKeyEvent;
import com.ads.appgm.util.SettingsUtils;

public class PaniqueManager implements DeviceManagerListener{

    private static PaniqueManager mInstance;
    private final Context mContext;
    private PaniqueManagerListener mListener;
    private boolean togglePanicIssued = false;
    private ScreenState currentScreenState = ScreenState.SCREEN_ON;
    private VolumeProviderCompat mVolumeChangeProvider;

    private PaniqueManager(Context context) {
        super();
        this.mContext = context;

        DeviceManager.getInstance(this.mContext).setListener(this);
    }

    public static PaniqueManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PaniqueManager(context);
        }
        return mInstance;
    }

    public void destroy() {
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

    public void setVolumeProvider(VolumeProviderCompat volumeProvider) {
        this.mVolumeChangeProvider = volumeProvider;
    }

    public void togglePanic() {
        DeviceManager.getInstance(this.mContext).togglePanic();
    }

    public boolean getPanicStatus() {
        return DeviceManager.getInstance(this.mContext).getPanicStatus();
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
    public void onPanicStatusChanged(boolean status) {
        if (WakeLockManager.getInstance().isHeldOnDemand() && !status) {
            WakeLockManager.getInstance().release();
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
}
