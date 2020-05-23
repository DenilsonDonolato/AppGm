package com.ads.appgm.manager.device.input.key.volume.rocker;

import android.content.Context;
import android.view.InputEvent;

import com.ads.appgm.manager.device.input.event.VolumeKeyEvent;
import com.ads.appgm.manager.device.input.key.volume.VolumeKeyDevice;
import com.ads.appgm.util.Constants;

public class VolumeKeyRocker extends VolumeKeyDevice {
    public static final String TYPE = Constants.ID_DEVICE_INPUT_VOLUMEKEY_ROCKER;

    private final static int MAX_BUFFER_SIZE = 6;
    private int[] buffer;
    private int current_ptr;

    public VolumeKeyRocker(Context context) {
        super(context);
        this.deviceType = TYPE;
        buffer = new int[MAX_BUFFER_SIZE];
    }

    @Override
    public boolean setEvent(InputEvent event) {
        VolumeKeyEvent volumeKeyEvent = (VolumeKeyEvent) event;
        if (volumeKeyEvent.getVolumeKeyEventType() == VolumeKeyEvent.VOLUME_KEY_EVENT_ROCKER && volumeKeyEvent.isVolumeKeyEvent()) {
            buffer[current_ptr] = volumeKeyEvent.getCurrentValue();
            current_ptr++;
            if (current_ptr == MAX_BUFFER_SIZE) {
                current_ptr = 0;
            }
            return this.isActionModePerformed();
        }
        return false;
    }

    @Override
    protected boolean isKeyComboPerformed() {
        boolean keyComboPerformed = false;
        for (int i = 0; i < current_ptr; i++) {
            if (i > 0) {
                int a = buffer[i], b = buffer[i - 1];
                if ((a == -1 && b == 1) || (a == 1 && b == -1)) {
                    this.clearBuffer();
                    keyComboPerformed = true;
                }
            }
        }
        if (keyComboPerformed) {
            this.updateCurrentSignal(INP_TRIGGER);
        }
        return keyComboPerformed;
    }

    private void clearBuffer() {
        buffer = new int[MAX_BUFFER_SIZE];
        current_ptr = 0;
    }
}