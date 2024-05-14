package com.example.xiaoshu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.*;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

public class TestActivity extends  AppCompatActivity{
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_detail);

        // 初始化 RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 创建笔记内容列表
        List<NoteItem> noteList = createNoteList();

        // 创建并设置适配器
        noteAdapter = new NoteAdapter(noteList);
        recyclerView.setAdapter(noteAdapter);
    }

    // 创建笔记内容列表的示例数据
    private List<NoteItem> createNoteList() {
        List<NoteItem> noteList = new ArrayList<>();
        noteList.add(new NoteItem(NoteItem.TYPE_TEXT, "这是一段文本内容"));
        noteList.add(new NoteItem(NoteItem.TYPE_AUDIO, "audio.mp3"));
        noteList.add(new NoteItem(NoteItem.TYPE_IMAGE, "image.jpg"));
        noteList.add(new NoteItem(NoteItem.TYPE_TEXT, "这是另一段文本内容"));
        noteList.add(new NoteItem(NoteItem.TYPE_AUDIO, "audio2.mp3"));
        noteList.add(new NoteItem(NoteItem.TYPE_IMAGE, "image2.jpg"));
        return noteList;
    }
}
