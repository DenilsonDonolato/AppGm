package com.ads.appgm.manager.device.output.panic.flashlight;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.ads.appgm.R;
import com.ads.appgm.util.Constants;

import java.io.IOException;
import java.util.List;

public class Flashlight1 extends Flashlight {

    public static final String TYPE = Constants.ID_DEVICE_OUTPUT_PANIC_FLASH_LEGACY;

    private Camera mCamera;
    private boolean flashSupported;

    public Flashlight1(Context context) {
        super(context);
        this.flashSupported = false;
        this.deviceType = TYPE;
    }

    @Override
    protected void turnOn() {
        if (this.ready() && !this.getStatus()) {
            try {
                this.mCamera.setPreviewTexture(new SurfaceTexture(0));//Thanks to http://github.com/bleeding182/minimalist-flashlight
                this.mCamera.startPreview();
//                  this.mCamera.autoFocus(null); //Causing error in Oxygen OS 2.2.0 (based on Android 5.1.1) marked by Piotr Zaborowski
                this.updateStatus(true);
            } catch (IOException previewUnavailable) {
                this.updateError(this.mContext.getResources().getString(R.string.camera_error));
                if (this.mCamera != null) {
                    try {
                        this.mCamera.release();
                        this.mCamera = null;
                    } catch (Exception ex) {
                    }
                }
            }
        }
        //TODO: Make it run in AsyncTask
    }

    @Override
    protected void turnOff() {
        if (this.getStatus() && this.mCamera != null) {
            this.mCamera.stopPreview();
            this.mCamera.release();
            this.mCamera = null;
            this.updateStatus(false);
        }
    }

    private boolean ready() {
        if (this.mCamera == null) {
            try {
                this.mCamera = Camera.open();
            } catch (RuntimeException cameraNotFound) {
                this.updateError(this.mContext.getResources().getString(R.string.camera_busy));
                return false;
            }
        }
        Camera.Parameters mCameraParameters = this.mCamera.getParameters();
        List<String> supportedFlashModes = mCameraParameters.getSupportedFlashModes();
        if (supportedFlashModes != null) {
            if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                this.flashSupported = true;
                mCameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            } else if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                this.flashSupported = true;
                mCameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            }
        }
        if (this.flashSupported) {
            try {
                mCamera.setParameters(mCameraParameters);
            } catch (RuntimeException FeatureUnsupported) {
                this.updateError(this.mContext.getResources().getString(R.string.panic_unsupported));
                return false;
            }
        }
        return true;
    }

}
