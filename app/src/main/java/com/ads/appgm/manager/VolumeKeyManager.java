package com.ads.appgm.manager;

import android.content.Context;
import android.os.Build;

import com.ads.appgm.manager.device.input.InputDeviceListener;
import com.ads.appgm.manager.device.input.event.VolumeKeyEvent;
import com.ads.appgm.manager.device.input.key.volume.VolumeKeyDevice;
import com.ads.appgm.manager.device.input.key.volume.nativve.VolumeKeyNative;
import com.ads.appgm.manager.device.input.key.volume.rocker.VolumeKeyRocker;

public class VolumeKeyManager {
    private static VolumeKeyManager mInstance;

    private VolumeKeyDevice volumeKeyDevice;
    private Context mContext;

    private boolean isEnabled;
    private InputDeviceListener mListener;

    private VolumeKeyManager(Context context, String volumeKeyType, boolean enable) {
        this.isEnabled = enable;
        this.mContext = context;
        this.setType(volumeKeyType);
    }

    public static VolumeKeyManager getInstance(Context context, String volumeKeyType, boolean enable) {
        if (mInstance == null) {
            mInstance = new VolumeKeyManager(context, volumeKeyType, enable);
        }
        return mInstance;
    }

    public void setEnabled(boolean enable) {
        this.isEnabled = enable;
        if (this.volumeKeyDevice != null) {
            this.volumeKeyDevice.setEnabled(this.isEnabled);
        }
    }

    public void setType(String volumeKeyType) {
        volumeKeyType = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) ? volumeKeyType : VolumeKeyRocker.TYPE;
        if (this.volumeKeyDevice != null && VolumeKeyDevice.TYPE.equals(volumeKeyType)) {
            return;
        }
        this.volumeKeyDevice = null;
        if (volumeKeyType.equals(VolumeKeyNative.TYPE)) {
            this.volumeKeyDevice = new VolumeKeyNative(this.mContext);
            this.volumeKeyDevice.setListener(this.mListener);
        } else if (volumeKeyType.equals(VolumeKeyRocker.TYPE)) {
            this.volumeKeyDevice = new VolumeKeyRocker(this.mContext);
            this.volumeKeyDevice.setListener(this.mListener);
        }
        this.volumeKeyDevice.setEnabled(this.isEnabled);
    }

    public boolean setVolumeKeyEvent(VolumeKeyEvent keyEvent) {
        return this.volumeKeyDevice.setInputEvent(keyEvent);
    }

    public void setListener(InputDeviceListener listener) {
        this.mListener = listener;
        if (this.volumeKeyDevice != null) {
            this.volumeKeyDevice.setListener(this.mListener);
        }
    }
}