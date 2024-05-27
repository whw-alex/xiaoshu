package com.example.xiaoshu;

public class NoteItem {
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_TEXT_PLACEHOLDER = 3;

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
    public void setType(int type) {
        this.type = type;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
