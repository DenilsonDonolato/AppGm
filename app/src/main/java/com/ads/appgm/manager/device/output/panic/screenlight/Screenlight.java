package com.ads.appgm.manager.device.output.panic.screenlight;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.ads.appgm.manager.device.output.panic.Panic;
import com.ads.appgm.ui.activity.ScreenflashActivity;
import com.ads.appgm.util.Constants;

public class Screenlight extends Panic {
    public static final String TYPE = Constants.ID_DEVICE_OUTPUT_PANIC_SCREEN;
    public final static String CLOSE_ACTIVITY_IDENTIFIER = "com.ads.appgm.CLOSE_ACTIVITY";

    private ScreenlightOffReceiver screenlightOffReceiver;

    public Screenlight(Context context) {
        super(context);
        this.deviceType = TYPE;
    }

    @Override
    protected void turnOn() {
        Intent intent = new Intent(this.mContext, ScreenflashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.mContext.startActivity(intent);
        this.updateStatus(true);
    }

    @Override
    protected void turnOff() {
        this.mContext.sendBroadcast(new Intent(CLOSE_ACTIVITY_IDENTIFIER));
        screenlightOffReceiver = new ScreenlightOffReceiver();
        this.mContext.registerReceiver(screenlightOffReceiver, new IntentFilter(this.deviceType));
    }

    public class ScreenlightOffReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateStatus(false);
            if (intent.getAction().equals(deviceType)) {
                mContext.unregisterReceiver(screenlightOffReceiver);
                screenlightOffReceiver = null;
            }
        }
    }

}
