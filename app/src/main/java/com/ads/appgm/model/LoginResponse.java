package com.ads.appgm.model;

public class LoginResponse {

    private String token;
    private long id;
    private String name;

    public LoginResponse() {
    }

    public LoginResponse(String token, long id, String name) {
        this.token = token;
        this.id = id;
        this.name = name;
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
}
