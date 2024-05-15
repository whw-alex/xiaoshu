package com.example.xiaoshu.Response;

import java.util.List;

public class NoteInfoResponse {
    private String title;
    private String modified_time;

    public NoteInfoResponse(String title, String modified_time) {
        this.title = title;
        this.modified_time = modified_time;
    }


    public String getTitle() {
        return title;
    }

    public String getModifiedTime() {
        return modified_time;
    }



}
