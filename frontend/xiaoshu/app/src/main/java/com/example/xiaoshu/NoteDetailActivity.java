package com.example.xiaoshu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.*;

public class NoteDetailActivity extends  AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_detail);
    }
}
