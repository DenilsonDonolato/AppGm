package com.ads.appgm.manager.device.input;

public interface InputDeviceListener {
    void onValueChanged(String deviceType, int eventConstant);
}
