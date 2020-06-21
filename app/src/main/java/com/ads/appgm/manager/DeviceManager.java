package com.ads.appgm.manager;

import android.content.Context;

import com.ads.appgm.manager.device.input.InputDevice;
import com.ads.appgm.manager.device.input.InputDeviceListener;
import com.ads.appgm.manager.device.input.event.VolumeKeyEvent;
import com.ads.appgm.manager.device.input.key.volume.nativve.VolumeKeyNative;
import com.ads.appgm.manager.device.input.key.volume.rocker.VolumeKeyRocker;
import com.ads.appgm.manager.device.input.proximity.ProximitySensor;
import com.ads.appgm.manager.device.output.OutputDeviceListener;
import com.ads.appgm.manager.device.output.panic.flashlight.Flashlight;
import com.ads.appgm.manager.device.output.vibrator.Vibrator;

public class DeviceManager implements OutputDeviceListener, InputDeviceListener {
    private static DeviceManager mInstance;
    private Context mContext;

    private DeviceManagerListener mListener;

    private DeviceManager(Context context) {
        this.mContext = context;

        PanicManager.getInstance(Flashlight.TYPE, true).setListener(this);
        VolumeKeyManager.getInstance(this.mContext, VolumeKeyNative.TYPE, true).setListener(this);
        ProximitySensor.getInstance(this.mContext).setListener(this);
    }

    public static DeviceManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DeviceManager(context);
        }
        return mInstance;
    }

    public void vibrate() {
        new Vibrator(this.mContext).vibrate();
    }

    public void setPanicType(String torchType) {
        PanicManager.getInstance(Flashlight.TYPE, true).setPanicType(torchType);
    }

    public void setPanicTimeout(int timeoutSec) {
        PanicManager.getInstance(Flashlight.TYPE, true).setTimeout(timeoutSec);
    }

    public void toggleTorch() {
        PanicManager.getInstance(Flashlight.TYPE, true).toggle(this.mContext);
    }

    public boolean getPanicStatus() {
        return PanicManager.getInstance(Flashlight.TYPE, true).getStatus();
    }

    public boolean setVolumeKeyEvent(VolumeKeyEvent volumeKeyEvent) {
        return VolumeKeyManager.getInstance(this.mContext, VolumeKeyNative.TYPE, true).setVolumeKeyEvent(volumeKeyEvent);
    }

    public void setVolumeKeyDeviceEnabled(boolean enabled) {
        VolumeKeyManager.getInstance(this.mContext, VolumeKeyNative.TYPE, true).setEnabled(enabled);
    }

    public void setVolumeKeyDeviceType(String volumeKeyType) {
        VolumeKeyManager.getInstance(this.mContext, VolumeKeyNative.TYPE, true).setType(volumeKeyType);
    }

    public void getProximityValue() {
        ProximitySensor.getInstance(this.mContext).getStatusRequest();
    }

    public void setListener(DeviceManagerListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onValueChanged(String deviceType, int eventConstant) {
        if (deviceType.equals(VolumeKeyNative.TYPE) || deviceType.equals(VolumeKeyRocker.TYPE)) {
            if (eventConstant == InputDevice.INP_TRIGGER) {
                if (this.mListener != null) {
                    this.mListener.onKeyActionPerformed();
                }
            }
        } else if (deviceType.equals(ProximitySensor.TYPE)) {
            if (eventConstant == InputDevice.INP_HIGH || eventConstant == InputDevice.INP_LOW) {
                if (this.mListener != null) {
                    this.mListener.onProximityChanged(!(eventConstant == InputDevice.INP_HIGH));
                }
            }
        }
    }

    @Override
    public void onStatusChanged(String deviceType, boolean status) {
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
