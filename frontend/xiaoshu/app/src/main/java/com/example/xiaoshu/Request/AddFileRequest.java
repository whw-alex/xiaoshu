package com.example.xiaoshu.Request;

public class AddFileRequest {
    Integer id;
    String filename;
    String location;
    String label;

    public AddFileRequest(Integer id_, String name, String loc, String label_) {
        id = id_;
        filename = name;
        location = loc;
        label = label_;
    }
}
