package com.example.xiaoshu.Request;

public class ProfileRequest {
    private String username;
    private String signature;
    private String avatar;
    private int id;
    public ProfileRequest(String username, String signature, String avatar, int id) {
        this.username = username;
        this.signature = signature;
        this.avatar = avatar;
        this.id = id;
    }
}
