package com.example.xiaoshu;

public class NoteItem {
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_IMAGE = 2;

    private int type;
    private String content;

    public NoteItem(int type, String content) {
        this.type = type;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}
