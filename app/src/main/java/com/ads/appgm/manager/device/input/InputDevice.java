package com.ads.appgm.manager.device.input;

import android.content.Context;
import android.view.InputEvent;

import com.ads.appgm.manager.device.Device;
import com.ads.appgm.util.Constants;

public abstract class InputDevice extends Device {
    public static final String TYPE = Constants.ID_DEVICE_INPUT;
    public final static int INP_LOW = 0;
    public final static int INP_HIGH = 1;
    public final static int INP_TRIGGER = 2;
    private InputDeviceListener mListener;

    public InputDevice(Context context) {
        super(context);
        this.deviceType = TYPE;
    }

    public final void setListener(InputDeviceListener listener) {
        this.mListener = listener;
    }

    public final boolean setInputEvent(InputEvent event) {
        if (this.isEnabled) {
            return this.setEvent(event);
        }
        return false;
    }

    protected abstract boolean setEvent(InputEvent event);

    public abstract void getStatusRequest();

    protected final void updateCurrentSignal(int signal) {
        if (this.mListener != null) {
            this.mListener.onValueChanged(this.deviceType, signal);
        }
    }

}
