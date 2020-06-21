package com.ads.appgm.manager.device.input;

import com.ads.appgm.manager.device.DeviceListener;

public interface InputDeviceListener extends DeviceListener {
    void onValueChanged(String deviceType, int eventConstant);
}
