package com.ads.appgm.manager.wakelock;

import android.content.ComponentName;
import android.content.Context;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.media.VolumeProviderCompat;

import com.ads.appgm.receiver.RockerReceiver;

public class WakeLock {
    public static final String TYPE = "com.ads.appgm.Wakelock";

    private MediaSessionCompat mMediaSession;
    private boolean isWakelockHeld;
    private boolean isEnabled;

    public WakeLock() {
        this.isWakelockHeld = false;
        this.isEnabled = true;
    }

    public void acquire(Context context, VolumeProviderCompat volumeProvider) {
        if (!this.isWakelockHeld && this.isEnabled) {
            if (this.mMediaSession == null) {
                ComponentName mediaReceiver = new ComponentName(context, RockerReceiver.class.getName());
                this.mMediaSession = new MediaSessionCompat(context, TYPE, mediaReceiver, null);

                this.mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
                this.mMediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING, 0, 0)
                        .build());
            }
            this.mMediaSession.setPlaybackToRemote(volumeProvider);
            this.mMediaSession.setActive(true);
            this.isWakelockHeld = true;
        }
    }

    public void release() {
        if (this.isWakelockHeld && this.isEnabled) {
            if (this.mMediaSession != null) {
                this.mMediaSession.setActive(false);
                this.mMediaSession.release();
                this.mMediaSession = null;
                this.isWakelockHeld = false;
            }
        }
    }

    public boolean isHeld() {
        return this.isWakelockHeld;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

}
