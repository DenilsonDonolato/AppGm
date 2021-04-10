package com.ads.appgm.model;

public class LoginExpired {
    private boolean measureExpired;

    public LoginExpired(boolean measureExpired) {
        this.measureExpired = measureExpired;
    }

    public boolean isMeasureExpired() {
        return measureExpired;
    }

    public void setMeasureExpired(boolean measureExpired) {
        this.measureExpired = measureExpired;
    }

    @Override
    public String toString() {
        return "LoginExpired{" +
                "measureExpired=" + measureExpired +
                '}';
    }
}
