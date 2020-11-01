package com.ads.appgm.model;

import androidx.annotation.NonNull;

import java.util.List;

public class MyLocation {

    @NonNull
    private List<Double> position;

    private boolean panic;

    public MyLocation(@NonNull List<Double> position, boolean panic) {
        this.position = position;
        this.panic = panic;
    }

    @NonNull
    public List<Double> getPosition() {
        return position;
    }

    public void setPosition(@NonNull List<Double> position) {
        this.position = position;
    }

    public boolean isPanic() {
        return panic;
    }

    public void setPanic(boolean panic) {
        this.panic = panic;
    }
}
