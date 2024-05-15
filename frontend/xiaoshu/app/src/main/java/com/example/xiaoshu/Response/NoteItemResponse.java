package com.example.xiaoshu.Response;

import com.example.xiaoshu.NoteItem;

public class NoteItemResponse {
    private String content;
    private String seg_type;
    private int index;

    public NoteItemResponse(String seg_type, String content, int index) {
        this.content = content;
        this.seg_type = seg_type;
        this.index = index;
    }

    public String getContent() {
        return content;
    }
    public int getType() {
        if (seg_type.equals("text")) {
            return NoteItem.TYPE_TEXT;
        } else if (seg_type.equals("image")) {
            return NoteItem.TYPE_IMAGE;
        } else {
            return NoteItem.TYPE_AUDIO;
        }
    }
    public int getIndex() {
        return index;
    }

}