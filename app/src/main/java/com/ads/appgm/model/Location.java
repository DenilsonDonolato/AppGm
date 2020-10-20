package com.ads.appgm.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Location {

    @NonNull
    private List<Double> position;

    public Location() {
        this.position = new ArrayList<>();
    }

    public Location(@NonNull List<Double> position) {
        this.position = position;
    }

    @NonNull
    public List<Double> getPosition() {
        return position;
    }

    public void setPosition(@NonNull List<Double> position) {
        this.position = position;
    }
}
