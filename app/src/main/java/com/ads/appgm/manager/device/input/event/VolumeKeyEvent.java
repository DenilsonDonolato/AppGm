package com.ads.appgm.manager.device.input.event;

import android.view.KeyEvent;

public class VolumeKeyEvent extends KeyEvent {
    public static final int VOLUME_KEY_EVENT_NATIVE = 0;
    public static final int VOLUME_KEY_EVENT_ROCKER = 1;

    private final int mVolumeKeyEventType;
    private final int mCurrentValue;

    public VolumeKeyEvent(int action, int code) {
        super(action, code);
        this.mVolumeKeyEventType = VOLUME_KEY_EVENT_NATIVE;
        this.mCurrentValue = 0;
    }

    public VolumeKeyEvent(long downTime, long eventTime, int action, int code, int repeat) {
        super(downTime, eventTime, action, code, repeat);
        this.mVolumeKeyEventType = VOLUME_KEY_EVENT_NATIVE;
        this.mCurrentValue = 0;
    }

    public VolumeKeyEvent(long downTime, long eventTime, int action, int code, int repeat, int metaState) {
        super(downTime, eventTime, action, code, repeat, metaState);
        this.mVolumeKeyEventType = VOLUME_KEY_EVENT_NATIVE;
        this.mCurrentValue = 0;
    }

    public VolumeKeyEvent(long downTime, long eventTime, int action, int code, int repeat, int metaState, int deviceId, int scancode) {
        super(downTime, eventTime, action, code, repeat, metaState, deviceId, scancode);
        this.mVolumeKeyEventType = VOLUME_KEY_EVENT_NATIVE;
        this.mCurrentValue = 0;
    }

    public VolumeKeyEvent(long downTime, long eventTime, int action, int code, int repeat, int metaState, int deviceId, int scancode, int flags) {
        super(downTime, eventTime, action, code, repeat, metaState, deviceId, scancode, flags);
        this.mVolumeKeyEventType = VOLUME_KEY_EVENT_NATIVE;
        this.mCurrentValue = 0;
    }

    public VolumeKeyEvent(long downTime, long eventTime, int action, int code, int repeat, int metaState, int deviceId, int scancode, int flags, int source) {
        super(downTime, eventTime, action, code, repeat, metaState, deviceId, scancode, flags, source);
        this.mVolumeKeyEventType = VOLUME_KEY_EVENT_NATIVE;
        this.mCurrentValue = 0;
    }

    public VolumeKeyEvent(long time, String characters, int deviceId, int flags) {
        super(time, characters, deviceId, flags);
        this.mVolumeKeyEventType = VOLUME_KEY_EVENT_NATIVE;
        this.mCurrentValue = 0;
    }

    public VolumeKeyEvent(KeyEvent origEvent) {
        super(origEvent);
        this.mVolumeKeyEventType = VOLUME_KEY_EVENT_NATIVE;
        this.mCurrentValue = 0;
    }

    @Deprecated
    public VolumeKeyEvent(KeyEvent origEvent, long eventTime, int newRepeat) {
        super(origEvent, eventTime, newRepeat);
        this.mVolumeKeyEventType = VOLUME_KEY_EVENT_NATIVE;
        this.mCurrentValue = 0;
    }

    public VolumeKeyEvent(int volumeDirection) {
        super(ACTION_DOWN, KEYCODE_VOLUME_DOWN);
        this.mVolumeKeyEventType = VOLUME_KEY_EVENT_ROCKER;
        this.mCurrentValue = volumeDirection;
    }

    public int getVolumeKeyEventType() {
        return this.mVolumeKeyEventType;
    }

    public boolean isVolumeKeyEvent() {
        return (this.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) || (this.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP);
    }

    public int getCurrentValue() {
        return this.mCurrentValue;
    }

}
