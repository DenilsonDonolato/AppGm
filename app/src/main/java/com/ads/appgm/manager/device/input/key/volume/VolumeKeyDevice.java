package com.ads.appgm.manager.device.input.key.volume;

import android.content.Context;

import com.ads.appgm.manager.device.input.InputDevice;
import com.ads.appgm.util.Constants;

public abstract class VolumeKeyDevice extends InputDevice {
    public static final String TYPE = Constants.ID_DEVICE_INPUT_VOLUMEKEY;

    public static final int MODE_VOLUME_COMBO = 0;

    private int mMode;

    public VolumeKeyDevice(Context context) {
        super(context);
        this.mMode = MODE_VOLUME_COMBO;
        this.deviceType = TYPE;
    }

    public final int getMode() {
        return this.mMode;
    }

    public final void setMode(int mode) {
        this.mMode = mode;
    }

    @Override
    public void getStatusRequest() {
        this.isActionModePerformed();
    }

    protected final boolean isActionModePerformed() {
        switch (this.mMode) {
            case MODE_VOLUME_COMBO:
                return this.isKeyComboPerformed();
            default:
                return this.isKeyComboPerformed();
        }
    }

    protected abstract boolean isKeyComboPerformed();

}
