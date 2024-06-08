package com.example.xiaoshu.Request;

public class DeleteItemRequest {
    int id;
    String path;
    String type;
    int index;

    public DeleteItemRequest(int id, String path, String type, int index) {
        this.id = id;
        this.path = path;
        this.type = type;
        this.index = index;
    }
}
