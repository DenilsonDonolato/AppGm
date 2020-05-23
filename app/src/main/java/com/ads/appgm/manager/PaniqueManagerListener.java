package com.ads.appgm.manager;

public interface PaniqueManagerListener {
    void onError(String error);

    void onPanicStatusChanged(boolean status);
}
