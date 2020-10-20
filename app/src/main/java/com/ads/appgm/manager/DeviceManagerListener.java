package com.ads.appgm.manager;

public interface DeviceManagerListener {
    void onPanicStatusChanged(boolean status);

    void onKeyActionPerformed();

    void onError(String error);
}
