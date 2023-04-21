package eus.ehu.chismosas.mastodonfx.domain;

public class AccountLoginInfo {

    public String id;
    public String token;

    public AccountLoginInfo(String token, String id) {
        this.token = token;
        this.id = id;
    }
}
