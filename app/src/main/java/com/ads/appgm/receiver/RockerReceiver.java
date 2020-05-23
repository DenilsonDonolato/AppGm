package com.ads.appgm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.ads.appgm.service.PaniqueQuick;

public class RockerReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            final KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_VOLUME_UP:
                        if (this.isTorchieQuickServiceRunning()) {
                            PaniqueQuick.getInstance().setVolumeValues(1);
                        }
                        break;
                    case KeyEvent.KEYCODE_VOLUME_DOWN:
                        if (this.isTorchieQuickServiceRunning()) {
                            PaniqueQuick.getInstance().setVolumeValues(-1);
                        }
                        break;
                    default:
                        if (this.isTorchieQuickServiceRunning()) {
                            PaniqueQuick.getInstance().setVolumeValues(0);
                        }
                }
            }
        }
    }

    private boolean isTorchieQuickServiceRunning() {
        return PaniqueQuick.getInstance() != null;
    }
}
