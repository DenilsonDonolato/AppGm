package com.ads.appgm.manager.device.input.key.volume.nativve;

import android.content.Context;
import android.view.InputEvent;
import android.view.KeyEvent;

import com.ads.appgm.manager.device.input.event.VolumeKeyEvent;
import com.ads.appgm.manager.device.input.key.volume.VolumeKeyDevice;
import com.ads.appgm.util.Constants;

public class VolumeKeyNative extends VolumeKeyDevice {
    public static final String TYPE = Constants.ID_DEVICE_INPUT_VOLUMEKEY_NATIVE;

    private boolean volumeDownPressed;
    private boolean volumeUpPressed;

    public VolumeKeyNative(Context context) {
        super(context);
        this.deviceType = TYPE;
        volumeDownPressed = false;
        volumeUpPressed = false;
    }

    @Override
    public boolean setEvent(InputEvent event) {
        VolumeKeyEvent volumeKeyEvent = (VolumeKeyEvent) event;
        if (volumeKeyEvent.getVolumeKeyEventType() == VolumeKeyEvent.VOLUME_KEY_EVENT_NATIVE && volumeKeyEvent.isVolumeKeyEvent()) {
            if (volumeKeyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (volumeKeyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    volumeDownPressed = true;
                } else if (volumeKeyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                    volumeUpPressed = true;
                }
            } else if (volumeKeyEvent.getAction() == KeyEvent.ACTION_UP) {
                if (volumeKeyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    volumeDownPressed = false;
                } else if (volumeKeyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                    volumeUpPressed = false;
                }
            }
            return this.isActionModePerformed();
        }
        return false;
    }


    protected boolean isKeyComboPerformed() {
        boolean keyComboPerformed = volumeDownPressed && volumeUpPressed;
        if (keyComboPerformed) {
            this.updateCurrentSignal(INP_TRIGGER);
        }
        return keyComboPerformed;
    }

}
