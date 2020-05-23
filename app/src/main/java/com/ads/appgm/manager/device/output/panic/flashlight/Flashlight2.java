package com.ads.appgm.manager.device.output.panic.flashlight;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;

import com.ads.appgm.R;
import com.ads.appgm.util.Constants;

@TargetApi(23)
public class Flashlight2 extends Flashlight {
    public static final String TYPE = Constants.ID_DEVICE_OUTPUT_PANIC_FLASH_NEW;

    private String[] mCameraIDList;
    private boolean flashSupported;

    public Flashlight2(Context context) {
        super(context);
        flashSupported = false;
        this.deviceType = TYPE;
    }

    @Override
    protected void turnOn() {
        if (!this.getStatus()) {
            CameraManager mCameraManager = (CameraManager) this.mContext.getSystemService(Context.CAMERA_SERVICE);
            try {
                this.mCameraIDList = mCameraManager.getCameraIdList();
            } catch (CameraAccessException e) {
                this.updateError(this.mContext.getResources().getString(R.string.camera_error));
                return;
            }
            try {
                CameraCharacteristics mCameraParameters = mCameraManager.getCameraCharacteristics(this.mCameraIDList[0]);
                this.flashSupported = mCameraParameters.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            } catch (Exception e) {
                this.updateError(this.mContext.getResources().getString(R.string.panic_unsupported));
                return;
            }
            if (this.flashSupported) {
                try {
                    mCameraManager.setTorchMode(this.mCameraIDList[0], true);
                    this.updateStatus(true);
                } catch (CameraAccessException e) {
                    this.updateError(this.mContext.getResources().getString(R.string.camera_busy));
                }
            }
        }
    }

    @Override
    protected void turnOff() {
        if (this.getStatus()) {
            if (this.mCameraIDList != null && this.flashSupported) {
                CameraManager mCameraManager = (CameraManager) this.mContext.getSystemService(Context.CAMERA_SERVICE);
                try {
                    mCameraManager.setTorchMode(mCameraIDList[0], false);
                } catch (CameraAccessException e) {
                    this.updateError(this.mContext.getResources().getString(R.string.panic_unsupported));
                    return;
                }
                this.updateStatus(false);
            }
        }
    }

}
