package com.ads.appgm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ads.appgm.R;
import com.ads.appgm.service.PaniqueQuick;
import com.ads.appgm.util.Constants;
import com.ads.appgm.util.MyNotification;
import com.ads.appgm.util.NotificationUtils;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Receiver", intent.getAction());
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if (PaniqueQuick.getInstance() == null) {
                NotificationUtils.sendNotification(context, context.getResources().getString(R.string.notify_title), context.getResources().getString(R.string.notify_text));
            }
        } else if (intent.getAction().equals("com.ads.appgm.notification")) {
            int notificationId = intent.getIntExtra(Constants.EXTRA_STARTED_FROM_NOTIFICATION, 0);
            if (notificationId > 0) {
                MyNotification myNotification = MyNotification.getInstance(context);
                myNotification.cancel(notificationId);
            }
        }
    }
}
