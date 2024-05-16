package com.example.xiaoshu.Response;

public class ProfileResponse {
    private String username;
    private String signature;
    private String avatar;
    private int id;
    public ProfileResponse(String username, String signature, String avatar, int id) {
        this.username = username;
        this.signature = signature;
        this.avatar = avatar;
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public String getSignature() {
        return signature;
    }
    public String getAvatar() {
        return avatar;
    }
    public int getId() {
        return id;
    }
}
