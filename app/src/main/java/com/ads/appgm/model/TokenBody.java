package com.ads.appgm.model;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.math.BigInteger;

public class TokenBody {

    @JsonAlias(value = "iat")
    private BigInteger issuedAt;
    @JsonAlias(value = "exp")
    private BigInteger expirationTime;
    @JsonAlias(value = "aud")
    private String audience;
    @JsonAlias(value = "sub")
    private String subject;

    public BigInteger getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(BigInteger issuedAt) {
        this.issuedAt = issuedAt;
    }

    public BigInteger getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(BigInteger expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
