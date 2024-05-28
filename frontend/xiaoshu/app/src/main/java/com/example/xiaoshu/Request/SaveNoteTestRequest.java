package com.example.xiaoshu.Request;

import android.util.Pair;

import java.util.List;

public class SaveNoteTestRequest {
    private int id;
    private String path;
    private List<Pair<Integer, String>> textList;

    public SaveNoteTestRequest(int id, String path, List<Pair<Integer, String>> textList) {
        this.id = id;
        this.path = path;
        this.textList = textList;
    }
}
