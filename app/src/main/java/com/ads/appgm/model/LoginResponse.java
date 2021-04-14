package com.ads.appgm.model;

public class LoginResponse {

    private String token;
    private long id;
    private String name;
    private long measureId;
    private String measureValidity;

    public LoginResponse() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMeasureId() {
        return measureId;
    }

    public void setMeasureId(long measureId) {
        this.measureId = measureId;
    }

    public String getMeasureValidity() {
        return measureValidity;
    }

    public void setMeasureValidity(String measureValidity) {
        this.measureValidity = measureValidity;
    }


    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", measureId=" + measureId +
                ", measureValidity='" + measureValidity + '\'' +
                '}';
    }
}
