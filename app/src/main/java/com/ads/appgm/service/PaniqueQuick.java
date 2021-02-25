package com.ads.appgm.service;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.media.VolumeProviderCompat;

import com.ads.appgm.manager.PaniqueManager;
import com.ads.appgm.manager.PaniqueManagerListener;
import com.ads.appgm.manager.ScreenState;
import com.ads.appgm.manager.device.input.event.VolumeKeyEvent;

public class PaniqueQuick extends AccessibilityService implements PaniqueManagerListener {

    private static PaniqueQuick mInstance;
    private ScreenStateReceiver mScreenStateReceiver;

    private PaniqueManagerListener mListener;

    public PaniqueQuick() {
        super();
    }

    @Nullable
    public static PaniqueQuick getInstance() {
        return mInstance;
    }

    public void setScreenState(ScreenState currentScreenState) {
        PaniqueManager.getInstance(this).setVolumeProvider(this.getVolumeChangeProvider());
    }

    public void setVolumeValues(int volumeDirection) {
        PaniqueManager.getInstance(this).setVolumeValue(volumeDirection);
    }

    private void registerScreenStateReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mScreenStateReceiver = new ScreenStateReceiver();
        registerReceiver(mScreenStateReceiver, filter);
    }

    private void unregisterScreenStateReceiver() {
        unregisterReceiver(mScreenStateReceiver);
    }

    private VolumeChangeProvider getVolumeChangeProvider() {
        AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int STREAM_TYPE = AudioManager.STREAM_MUSIC;
        int currentVolume = audio.getStreamVolume(STREAM_TYPE);
        int maxVolume = audio.getStreamMaxVolume(STREAM_TYPE);
        return new VolumeChangeProvider(VolumeProviderCompat.VOLUME_CONTROL_RELATIVE, maxVolume, currentVolume);
    }

    public void registerPaniqueManagerListener(PaniqueManagerListener listener) {
        this.mListener = listener;
    }

    public void unregisterPaniqueManagerListener() {
        this.mListener = null;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mInstance = this;
        registerScreenStateReceiver();
        PaniqueManager.getInstance(this).setListener(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        PaniqueManager.getInstance(this).destroy();
        unregisterScreenStateReceiver();
        mInstance = null;
        return super.onUnbind(intent);
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        super.onKeyEvent(event);
        if ((event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) || (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP)) { //Filters ONLY the Volume Button key events
            PaniqueManager.getInstance(this).setVolumeKeyEvent(new VolumeKeyEvent(event));
        }
        return false;
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPanicStatusChanged(boolean status) {
        if (this.mListener != null) {
            this.mListener.onPanicStatusChanged(status);
        }
    }

    public void togglePanic() {
        PaniqueManager.getInstance(this).togglePanic();
    }

    public boolean getPanicStatus() {
        return PaniqueManager.getInstance(this).getPanicStatus();
    }

    public class ScreenStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ScreenState currentScreenState = ScreenState.SCREEN_ON;
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                currentScreenState = ScreenState.SCREEN_OFF;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                currentScreenState = ScreenState.SCREEN_LOCK;
            } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                currentScreenState = ScreenState.SCREEN_ON;
            }
            setScreenState(currentScreenState);
        }
    }

    public class VolumeChangeProvider extends VolumeProviderCompat {

        /**
         * Create a new volume provider for handling volume events. You must specify
         * the type of volume control and the maximum volume that can be used.
         *
         * @param volumeControl The method for controlling volume that is used by
         *                      this provider.
         * @param maxVolume     The maximum allowed volume.
         * @param currentVolume The current volume.
         */
        public VolumeChangeProvider(int volumeControl, int maxVolume, int currentVolume) {
            super(volumeControl, maxVolume, currentVolume);
        }

        @Override
        public void onAdjustVolume(int direction) {
            // Up = 1, Down = -1, Release = 0
            setVolumeValues(direction);
            Log.d("panic", String.valueOf(direction));
        }
    }
}
