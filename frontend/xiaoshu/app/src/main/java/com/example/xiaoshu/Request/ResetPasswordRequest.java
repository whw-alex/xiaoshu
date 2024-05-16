package com.example.xiaoshu.Request;

public class ResetPasswordRequest {
    private int id;
    private String old_password;
    private String new_password;

    public ResetPasswordRequest(int id, String old_password, String new_password) {
        this.id = id;
        this.old_password = old_password;
        this.new_password = new_password;
    }
}
