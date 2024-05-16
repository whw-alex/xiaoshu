package com.example.xiaoshu.Response;

public class LoginResponse {
    private int id;
    private String username;
    private String avatar;
    private String signature;

    public LoginResponse(String username, String avatar, int id, String signature) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.signature = signature;
    }
    public int getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }

    public String getAvatar() {
        return avatar;
    }
    public String getSignature() {
        return signature;
    }


}
