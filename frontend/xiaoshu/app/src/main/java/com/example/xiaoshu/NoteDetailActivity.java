package com.example.xiaoshu;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.*;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xiaoshu.R;
import com.example.xiaoshu.Request.AddFileRequest;
import com.example.xiaoshu.Request.NoteDetailRequest;
import com.example.xiaoshu.Request.SaveNoteTestRequest;
import com.example.xiaoshu.Response.AddFileResponse;
import com.example.xiaoshu.Response.NoteInfoResponse;
import com.example.xiaoshu.Response.NoteItemResponse;

import android.view.*;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.util.Locale;

import com.google.android.material.appbar.MaterialToolbar;
import android.Manifest;


public class NoteDetailActivity extends  AppCompatActivity{
    MaterialToolbar topAppBar;
    private NoteAdapter noteAdapter;
    RecyclerView recyclerView;
    List<NoteItem> noteList;
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 0x00000012;
    private Uri imageUri;
    private Uri mCameraUri;
    private String path;

    // 用于保存图片的文件路径，Android 10以下使用图片路径访问图片
    private String mCameraImagePath;

    // 是否是Android 10以上手机
    private boolean isAndroidQ = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_detail);
        path = getIntent().getStringExtra("path");

        noteList = new ArrayList<>();
        TextView title = findViewById(R.id.title);
        TextView modifiedTime = findViewById(R.id.modified_time);

        // 初始化 RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences("login_status", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int id = sharedPreferences.getInt("id", 0);
        API api = API.Creator.createApiService();
        Call<NoteInfoResponse> call_ = api.noteInfo(new NoteDetailRequest(path, id));
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
                Toasty.error(NoteDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });

        Call<List<NoteItemResponse>> call = api.noteDetail(new NoteDetailRequest(path, id));

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
//                    noteList.add(new NoteItem(NoteItem.TYPE_TEXT, "这是一段文本内容"));
//                    noteList.add(new NoteItem(NoteItem.TYPE_AUDIO, "audio.mp3"));
//                    noteList.add(new NoteItem(NoteItem.TYPE_TEXT, "这是另一段文本内容"));
//                    noteList.add(new NoteItem(NoteItem.TYPE_AUDIO, "audio2.mp3"));
//                    noteList.add(new NoteItem(NoteItem.TYPE_IMAGE, ""));
//                    noteList.add(new NoteItem(NoteItem.TYPE_TEXT_PLACEHOLDER, ""));

                    // 创建并设置适配器
                    noteAdapter = new NoteAdapter(noteList);
                    recyclerView.setAdapter(noteAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<NoteItemResponse>> call, Throwable t) {
                // 请求失败
                Log.d("TestActivity", "Error: " + t.getMessage());
                Toasty.error(NoteDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });


        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> {
            saveAllText();
//            finish();
        });
        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.item_take_photo) {
                    // camera
                    Log.d("NoteDetailActivity", "take photo");
                    checkPermissionAndCamera();

//                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(takePictureIntent, PERMISSION_CAMERA_REQUEST_CODE);
//
//                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                        Log.d("NoteDetailActivity", "start camera");
//                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                    }

                    return true;
                } else if (itemId == R.id.item_choose_photo) {
//                    choose photo
                    Log.d("NoteDetailActivity", "choose photo");
                    pickImageFromGallery();


                    return true;
                } else if (itemId == R.id.item_audio) {
                    // 更改icon
                    if (item.getIcon().getConstantState().equals(getResources().getDrawable(R.drawable.ic_audio).getConstantState())) {
                        item.setIcon(R.drawable.ic_stop_audio);
                    } else {
                        item.setIcon(R.drawable.ic_audio);
                    }
                    return true;
                }

                return false;
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("NoteDetailActivity", "onActivityResult");

        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {



//             获取notelist
            Log.d("NoteDetailActivity", "Image URL: " + mCameraUri.toString());
//            noteList.remove(noteList.size() - 1);
            noteList.add(new NoteItem(NoteItem.TYPE_IMAGE, mCameraUri.toString()));
            noteList.add(new NoteItem(NoteItem.TYPE_TEXT, "placeholder"));
//            noteList.add(new NoteItem(NoteItem.TYPE_TEXT_PLACEHOLDER, ""));
            noteAdapter.notifyDataSetChanged();
            uploadImage(mCameraUri);
        }
        else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.d("NoteDetailActivity", "Image URL: " + uri.toString());
//            noteList.remove(noteList.size() - 1);
            noteList.add(new NoteItem(NoteItem.TYPE_IMAGE, uri.toString()));
            noteList.add(new NoteItem(NoteItem.TYPE_TEXT, "placeholder"));
//            noteList.add(new NoteItem(NoteItem.TYPE_TEXT_PLACEHOLDER, ""));
            noteAdapter.notifyDataSetChanged();
            uploadImage(uri);
        }
    }



    private void checkPermissionAndCamera() {
        int hasCameraPermission = ContextCompat.checkSelfPermission(getApplication(),
                Manifest.permission.CAMERA);
        if (hasCameraPermission == PackageManager.PERMISSION_GRANTED) {
            //有调起相机拍照。
            Log.d("NoteDetailActivity", "checkPermissionAndCamera");
            openCamera();
        } else {
            //没有权限，申请权限。
            Log.d("NoteDetailActivity", "requestPermissions");
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},
                    PERMISSION_CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //允许权限，有调起相机拍照。
                openCamera();
            } else {
                //拒绝权限，弹出提示框。
                Toast.makeText(this, "拍照权限被拒绝", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void openCamera() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断是否有相机
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            Log.d("NoteDetailActivity", "openCamera");
            File photoFile = null;
            Uri photoUri = null;

            if (isAndroidQ) {
                // 适配android 10
                photoUri = createImageUri();
            } else {
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (photoFile != null) {
                    mCameraImagePath = photoFile.getAbsolutePath();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                        photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                    } else {
                        photoUri = Uri.fromFile(photoFile);
                    }
                }
            }

            mCameraUri = photoUri;
            if (photoUri != null) {
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(captureIntent, PERMISSION_CAMERA_REQUEST_CODE);
            }
        }
        else {
            Toast.makeText(this, "没有相机", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     */
    private Uri createImageUri() {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }

    }

    /**
     * 创建保存图片的文件
     */
    private File createImageFile() throws IOException {
        String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File tempFile = new File(storageDir, imageName);
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }
        return tempFile;
    }

    public void uploadImage(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
            byte[] data = os.toByteArray();
            Log.d("NoteDetailActivity", "uploadImage data: " + data.length);
            API api = API.Creator.createApiService();
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), data);
            Log.d("NoteDetailActivity", "uploadImage requestbody: " + requestBody.contentType() + " " + requestBody.contentLength() + " " + requestBody.toString());
            SharedPreferences sharedPreferences = getSharedPreferences("login_status", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            int id = sharedPreferences.getInt("id", 0);
            builder.addFormDataPart("id", String.valueOf(id));
            builder.addFormDataPart("file", "image.jpg", requestBody);
            builder.addFormDataPart("path", path);
            RequestBody body = builder.build();
            Log.d("NoteDetailActivity", "uploadImage: " + body.contentType() + " " + body.contentLength());
            Call<AddFileResponse> call = api.uploadImage(body);
            call.enqueue(new Callback<AddFileResponse>() {
                @Override
                public void onResponse(Call<AddFileResponse> call, Response<AddFileResponse> response) {
                    if(response.isSuccessful())
                    {
                        AddFileResponse addFileResponse = response.body();
                        Log.d("NoteDetailActivity", "AddFileResponse: " + addFileResponse.getMsg());
                    }
                }

                @Override
                public void onFailure(Call<AddFileResponse> call, Throwable t) {
                    // 请求失败
                    Log.d("NoteDetailActivity", "Error: " + t.getMessage());
                    Toasty.error(NoteDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT, true).show();
                }
            });
            os.close();
            is.close();
        } catch (IOException e) {
            Log.d("NoteDetailActivity", "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void saveAllText() {
        // 保存所有文本内容
        List<Pair<Integer, String>> textList = new ArrayList<>();
        for (int i = 0; i < noteList.size(); i++) {
            NoteItem item = noteList.get(i);
            if (item.getType() == NoteItem.TYPE_TEXT) {
                // 保存文本内容
                Log.d("NoteDetailActivity", "Save text: " + item.getContent());
//                textList.add(new Pair<>(i, item.getContent()));
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
                if (viewHolder instanceof NoteAdapter.TextViewHolder) {
                    NoteAdapter.TextViewHolder textViewHolder = (NoteAdapter.TextViewHolder) viewHolder;
                    textList.add(new Pair<>(i, textViewHolder.editText.getText().toString()));
                }
            }
        }
        SharedPreferences sharedPreferences = getSharedPreferences("login_status", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int id = sharedPreferences.getInt("id", 0);
        API api = API.Creator.createApiService();
        Call<AddFileResponse> call = api.saveNoteText(new SaveNoteTestRequest(id, path, textList));
        call.enqueue(new Callback<AddFileResponse>() {
            @Override
            public void onResponse(Call<AddFileResponse> call, Response<AddFileResponse> response) {
                if(response.isSuccessful())
                {
                    AddFileResponse addFileResponse = response.body();
                    Log.d("NoteDetailActivity", "AddFileResponse: " + addFileResponse.getMsg());
                    finish();
                }
                else
                {
                    Log.d("NoteDetailActivity", "Error: " + response.message());
                    Toasty.error(NoteDetailActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT, true).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<AddFileResponse> call, Throwable t) {
                // 请求失败
                Log.d("NoteDetailActivity", "Error: " + t.getMessage());
                Toasty.error(NoteDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT, true).show();
                finish();

            }
        });
    }

}
