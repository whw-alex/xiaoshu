package com.example.xiaoshu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.xiaoshu.Request.NoteDetailRequest;
import com.example.xiaoshu.Response.NoteItemResponse;
import com.example.xiaoshu.Response.NoteInfoResponse;
import com.google.android.material.appbar.MaterialToolbar;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toolbar;


public class TestActivity extends  AppCompatActivity{
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    MaterialToolbar topAppBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_detail);
        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> {
            finish();
        });
        List<NoteItem> noteList = new ArrayList<>();
        TextView title = findViewById(R.id.title);
        TextView modifiedTime = findViewById(R.id.modified_time);

        // 初始化 RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SharedPreferences sharedPreferences = getSharedPreferences("login_status", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int id = sharedPreferences.getInt("id", 0);
        API api = API.Creator.createApiService();
        Call<NoteInfoResponse> call_ = api.noteInfo(new NoteDetailRequest("", id));
        call_.enqueue(new Callback<NoteInfoResponse>() {
            @Override
            public void onResponse(Call<NoteInfoResponse> call, Response<NoteInfoResponse> response) {
                if(response.isSuccessful())
                {
                    NoteInfoResponse noteInfoResponse = response.body();
                    Log.d("TestActivity", "NoteInfoResponse: " + noteInfoResponse.getTitle()  + " " + noteInfoResponse.getModifiedTime());
                    title.setText(noteInfoResponse.getTitle());
                    modifiedTime.setText(noteInfoResponse.getModifiedTime());
                }
            }

            @Override
            public void onFailure(Call<NoteInfoResponse> call, Throwable t) {
                // 请求失败
                Log.d("TestActivity", "Error: " + t.getMessage());
                Toasty.error(TestActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });

        Call<List<NoteItemResponse>> call = api.noteDetail(new NoteDetailRequest("", 0));

        call.enqueue(new Callback<List<NoteItemResponse>>() {
            @Override
            public void onResponse(Call<List<NoteItemResponse>> call, Response<List<NoteItemResponse>> response) {
                if(response.isSuccessful())
                {
                    List<NoteItemResponse> noteItemResponses = response.body();
                    for(NoteItemResponse noteItemResponse : noteItemResponses)
                    {
                        noteList.add(new NoteItem(noteItemResponse.getType(), noteItemResponse.getContent()));
                        Log.d("TestActivity", "NoteItemResponse: " + noteItemResponse.getType() + " " + noteItemResponse.getContent());
                    }
                    // 创建笔记内容列表
                    noteList.add(new NoteItem(NoteItem.TYPE_TEXT, "这是一段文本内容"));
                    noteList.add(new NoteItem(NoteItem.TYPE_AUDIO, "audio.mp3"));
                    noteList.add(new NoteItem(NoteItem.TYPE_IMAGE, "image.jpg"));
                    noteList.add(new NoteItem(NoteItem.TYPE_TEXT, "这是另一段文本内容"));
                    noteList.add(new NoteItem(NoteItem.TYPE_AUDIO, "audio2.mp3"));
                    noteList.add(new NoteItem(NoteItem.TYPE_IMAGE, "image2.jpg"));

                    // 创建并设置适配器
                    noteAdapter = new NoteAdapter(noteList, getApplicationContext());
                    recyclerView.setAdapter(noteAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<NoteItemResponse>> call, Throwable t) {
                // 请求失败
                Log.d("TestActivity", "Error: " + t.getMessage());
                Toasty.error(TestActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });


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
