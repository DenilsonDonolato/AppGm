package com.ads.appgm.model;

public class LoginResponse {

    private String token;
    private long id;
    private String nome;
    private String validade;

    public LoginResponse() {
    }

    public LoginResponse(String token, long id, String nome, String validade) {
        this.token = token;
        this.id = id;
        this.nome = nome;
        this.validade = validade;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getValidade() {
        return validade;
    }

    public void setValidade(String validade) {
        this.validade = validade;
    }
}
