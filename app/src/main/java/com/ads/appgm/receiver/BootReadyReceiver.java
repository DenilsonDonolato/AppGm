package com.ads.appgm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ads.appgm.R;
import com.ads.appgm.service.PaniqueQuick;
import com.ads.appgm.util.NotificationUtils;

public class BootReadyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if (PaniqueQuick.getInstance() == null) {
                NotificationUtils.sendNotification(context, String.format(context.getResources().getString(R.string.notify_title)), String.format(context.getResources().getString(R.string.notify_text)));
            }
        }
    }
}
