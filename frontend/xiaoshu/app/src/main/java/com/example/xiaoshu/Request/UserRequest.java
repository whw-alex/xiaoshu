package com.example.xiaoshu.Request;

public class UserRequest {
    private String username;
    private String password;
    private String signature;

    public UserRequest(String username, String password, String signature)
    {
        this.username = username;
        this.password = password;
        this.signature = signature;
    }
}
