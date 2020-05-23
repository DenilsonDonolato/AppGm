package com.ads.appgm.manager.device.input.proximity;

import com.ads.appgm.manager.device.input.InputDevice;
import com.ads.appgm.manager.timer.CountTimer;
import com.ads.appgm.manager.timer.CountTimerListener;
import com.ads.appgm.util.Constants;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.InputEvent;

import static android.content.Context.SENSOR_SERVICE;

public class ProximitySensor extends InputDevice implements SensorEventListener, CountTimerListener {
    public static final String TYPE = Constants.ID_DEVICE_INPUT_PROXIMITY;
    private static ProximitySensor mInstance;
    private CountTimer mCountTimer;

    private int signal;

    private ProximitySensor(Context context) {
        super(context);
        this.deviceType = TYPE;
    }

    public static ProximitySensor getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ProximitySensor(context);
        }
        return mInstance;
    }

    @Override
    protected boolean setEvent(InputEvent event) {
        return false;
    }

    @Override
    public void getStatusRequest() {
        final SensorManager mSensorManager = (SensorManager) this.mContext.getSystemService(SENSOR_SERVICE);
        final Sensor proximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        signal = (event.values[0] == 0) ? INP_HIGH : INP_LOW;
        if (signal == INP_HIGH) {
            if (mCountTimer != null) {
                mCountTimer.cancel();
            }
            final SensorManager mSensorManager = (SensorManager) this.mContext.getSystemService(SENSOR_SERVICE);
            mSensorManager.unregisterListener(this);
            this.updateCurrentSignal(signal);
        } else {
            mCountTimer = new CountTimer("ProximitySensorResponse", 0.3f, this);
            mCountTimer.start();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onCountEnd(String id) {
        final SensorManager mSensorManager = (SensorManager) this.mContext.getSystemService(SENSOR_SERVICE);
        mSensorManager.unregisterListener(this);
        this.updateCurrentSignal(signal);
    }
}
