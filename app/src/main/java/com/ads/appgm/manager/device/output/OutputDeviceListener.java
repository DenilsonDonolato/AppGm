package com.ads.appgm.manager.device.output;

import com.ads.appgm.manager.device.DeviceListener;

public interface OutputDeviceListener extends DeviceListener {
    void onStatusChanged(String deviceType, boolean status);
}
