package com.example.xiaoshu;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
import com.example.xiaoshu.Request.NoteDetailRequest;
import com.example.xiaoshu.Response.NoteInfoResponse;
import com.example.xiaoshu.Response.NoteItemResponse;

import android.view.*;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.graphics.Bitmap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.File;
import java.util.Locale;

import com.google.android.material.appbar.MaterialToolbar;
import android.Manifest;


public class NoteDetailActivity extends  AppCompatActivity{
    MaterialToolbar topAppBar;
    private NoteAdapter noteAdapter;
    RecyclerView recyclerView;
    List<NoteItem> noteList;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 0x00000012;
    private Uri imageUri;
    private Uri mCameraUri;

    // 用于保存图片的文件路径，Android 10以下使用图片路径访问图片
    private String mCameraImagePath;

    // 是否是Android 10以上手机
    private boolean isAndroidQ = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_detail);


        noteList = new ArrayList<>();
        TextView title = findViewById(R.id.title);
        TextView modifiedTime = findViewById(R.id.modified_time);

        // 初始化 RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        API api = API.Creator.createApiService();
        Call<NoteInfoResponse> call_ = api.noteInfo(new NoteDetailRequest(""));
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

        Call<List<NoteItemResponse>> call = api.noteDetail(new NoteDetailRequest(""));

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
                    noteList.add(new NoteItem(NoteItem.TYPE_IMAGE, ""));
                    noteList.add(new NoteItem(NoteItem.TYPE_TEXT, "这是另一段文本内容"));
                    noteList.add(new NoteItem(NoteItem.TYPE_AUDIO, "audio2.mp3"));
                    noteList.add(new NoteItem(NoteItem.TYPE_IMAGE, ""));
                    noteList.add(new NoteItem(NoteItem.TYPE_TEXT_PLACEHOLDER, "这是最后一段文本内容"));

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
            finish();
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
                    // edit note
                    return true;
                } else if (itemId == R.id.item_audio) {
                    // delete note
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
//            Bundle extras = data.getExtras();
//             Bitmap imageBitmap = (Bitmap) extras.get("data");
//            // 将 Bitmap 对象保存到文件
//            File imageFile = saveBitmapToFile(imageBitmap);
//
//            // 获取文件路径作为 URL
//            String imageUrl = imageFile.getAbsolutePath();
//            Log.d("NoteDetailActivity", "Image URL: " + imageUrl);


//             获取notelist
            Log.d("NoteDetailActivity", "Image URL: " + mCameraUri.toString());
            noteList.remove(noteList.size() - 1);
            noteList.add(new NoteItem(NoteItem.TYPE_IMAGE, mCameraUri.toString()));
            noteList.add(new NoteItem(NoteItem.TYPE_TEXT_PLACEHOLDER, "这是最后一段文本内容"));
            noteAdapter.notifyDataSetChanged();
        }
    }

    private File saveBitmapToFile(Bitmap bitmap) {
        File filesDir = getFilesDir();
        File imageFile = new File(filesDir, "captured_image.jpg");

        try (OutputStream outputStream = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageFile;
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

}
