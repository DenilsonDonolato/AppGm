package com.ads.appgm.manager.device.output.panic;

import android.content.Context;
import android.content.Intent;

import com.ads.appgm.service.ForegroundLocationService;
import com.ads.appgm.util.Constants;

public class Panique2 extends Panique {

    private final Context context;

    public Panique2(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void turnOn() {
        Intent intent = new Intent(context, ForegroundLocationService.class);
        intent.putExtra(Constants.EXTRA_STARTED_FROM_PANICQUICK, true);
        intent.putExtra(Constants.PANIC, true);
        context.startService(intent);
        this.updateStatus(true);
    }

    @Override
    protected void turnOff() {
        turnOn();
    }
}