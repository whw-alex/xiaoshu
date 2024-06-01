package com.example.xiaoshu.Request;

public class DeleteFileRequest {
    int id;
    String path;
    String label;

    public  DeleteFileRequest(int id, String path, String label) {
        this.id = id;
        this.path = path;
        this.label = label;
    }

}
