package com.ads.appgm.manager;

public interface DeviceManagerListener {
    void onPanicStatusChanged(boolean status);

    void onKeyActionPerformed();

    void onProximityChanged(boolean status);

    void onError(String error);
}
