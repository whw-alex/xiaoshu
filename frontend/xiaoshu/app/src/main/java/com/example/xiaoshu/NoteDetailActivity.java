package com.example.xiaoshu;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.widget.EditText;
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
import com.example.xiaoshu.Request.AIChatRequest;
import com.example.xiaoshu.Request.AddFileRequest;
import com.example.xiaoshu.Request.NoteDetailRequest;
import com.example.xiaoshu.Request.SaveNoteTestRequest;
import com.example.xiaoshu.Request.UploadFakeImageRequest;
import com.example.xiaoshu.Response.AIChatResponse;
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
import com.kongzue.dialogx.dialogs.BottomDialog;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;

import android.Manifest;


public class NoteDetailActivity extends  AppCompatActivity{
    MaterialToolbar topAppBar;
    private NoteAdapter noteAdapter;
    RecyclerView recyclerView;
    List<NoteItem> noteList;
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 0x00000012;
    private static final int PERMISSION_AUDIO_REQUEST_CODE = 0x00000013;
    private Uri imageUri;
    private Uri mCameraUri;
    private String path;

    // 用于保存图片的文件路径，Android 10以下使用图片路径访问图片
    private String mCameraImagePath;

    // 是否是Android 10以上手机
    private boolean isAndroidQ = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;
    MediaRecorder recorder;
    boolean isRecording = false;
    File audioFile;

    // 以下是关于AI对话框的状态变量
    private enum DIALOG_STATE {
        INIT,
        CLOSE,
        WAIT_FOR_ANSWER,
        OBTAINED_ANSWER
    }
    private DIALOG_STATE state;
    private View dialog_ai;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_detail);
        path = getIntent().getStringExtra("path");

        isRecording = false;

        noteList = new ArrayList<>();
//        TextView title = findViewById(R.id.title);
        EditText title = findViewById(R.id.title);
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
                    noteAdapter = new NoteAdapter(noteList, getApplicationContext());
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
                        startRecord();

                    } else {
                        item.setIcon(R.drawable.ic_audio);
                        stopRecord();

                    }
                    return true;

                } else if (itemId == R.id.item_expand_ai) {
                    expandAIWindow();
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
            uploadFakeImage();
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
            uploadFakeImage();
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

    private void checkPermissionAndAudio() {
        int hasAudioPermission = ContextCompat.checkSelfPermission(getApplication(),
                Manifest.permission.RECORD_AUDIO);
        if (hasAudioPermission == PackageManager.PERMISSION_GRANTED) {
            //有调起相机拍照。
            Log.d("NoteDetailActivity", "checkPermissionAndAudio");
            record();
        } else {
            //没有权限，申请权限。
            Log.d("NoteDetailActivity", "requestPermissions");
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_AUDIO_REQUEST_CODE);
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
        else if (requestCode == PERMISSION_AUDIO_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //允许权限。
                Log.d("NoteDetailActivity", "openAudio");
                record();
            } else {
                //拒绝权限，弹出提示框。
                Toast.makeText(this, "录音权限被拒绝", Toast.LENGTH_LONG).show();
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

    private void record(){
        File fileEx = this.getExternalFilesDir(null);
        String dir = fileEx.getAbsolutePath() + "/MediaRecorderTest";
        File file_path = new File(dir);
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置播放源 麦克风
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); //设置输入格式 3gp
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); //设置编码 AMR
        if(!file_path.exists())
        {
            boolean is_dir_created = file_path.mkdirs();
            Log.d("NoteDetailActivity", "is_dir_created: " + is_dir_created);
        }

        try {
            //这个地方写文件名，可以利用时间来保存为不同的文件名
            String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            fileName = fileName + ".MP3";
            Log.d("NoteDetailActivity", "fileName: " + fileName);
            audioFile=new File(file_path,fileName);
            if(audioFile.exists())
            {
                audioFile.delete();
            }
            if (!audioFile.getParentFile().exists())
                audioFile.getParentFile().mkdirs();
            if (!audioFile.exists())
                audioFile.createNewFile();
            boolean is_created = audioFile.createNewFile();//创建文件
            Log.d("NoteDetailActivity", "is_Created: " + is_created);

        } catch (Exception e) {
            throw new RuntimeException("Couldn't create recording audio file", e);
        }

        recorder.setOutputFile(audioFile.getAbsolutePath());

        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            throw new RuntimeException("IllegalStateException on MediaRecorder.prepare", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException on MediaRecorder.prepare", e);
        }
        isRecording=true;
        recorder.start();
    }

    public void startRecord(){
        Toast.makeText(this,"开始录音",Toast.LENGTH_SHORT).show();
        checkPermissionAndAudio();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void stopRecord(){
        if(isRecording){
            Toast.makeText(this,"停止录音",Toast.LENGTH_SHORT).show();
            if (recorder != null){
                try {
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                    noteList.add(new NoteItem(NoteItem.TYPE_AUDIO, audioFile.getAbsolutePath()));
                    noteList.add(new NoteItem(NoteItem.TYPE_TEXT, "placeholder"));
                    noteAdapter.notifyDataSetChanged();
                    uploadMP3();
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    Uri uri = Uri.fromFile(audioFile);
//                    intent.setDataAndType(uri, "audio/mp3");
//                    startActivity(intent);



                } catch (IllegalStateException e) {
                    // TODO 如果当前java状态和jni里面的状态不一致，
                    //e.printStackTrace();
                    recorder = null;
                    recorder = new MediaRecorder();
                }

            }
            isRecording=false;
        }
    }

    public void uploadFakeImage()
    {
        API api = API.Creator.createApiService();
        SharedPreferences sharedPreferences = getSharedPreferences("login_status", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int id = sharedPreferences.getInt("id", 0);
        Call<AddFileResponse> call = api.uploadFakeImage(new UploadFakeImageRequest(id, path));
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

    public void uploadMP3() {
        API api = API.Creator.createApiService();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        File uploadFile = new File(audioFile.getAbsolutePath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("audio/mp3"), uploadFile);
        Log.d("NoteDetailActivity", "uploadMP3 requestbody: " + requestBody.contentType() + " " + requestBody.toString());
        SharedPreferences sharedPreferences = getSharedPreferences("login_status", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int id = sharedPreferences.getInt("id", 0);
        builder.addFormDataPart("id", String.valueOf(id));
        builder.addFormDataPart("file", "audio.mp3", requestBody);
        builder.addFormDataPart("path", path);
        RequestBody body = builder.build();
        Log.d("NoteDetailActivity", "uploadMP3: " + body.contentType());
        Call<AddFileResponse> call = api.uploadAudio(body);
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



    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void saveAllText() {
        // 保存所有文本内容
        List<Pair<Integer, String>> textList = new ArrayList<>();
//        先保存标题
        textList.add(new Pair<>(-1, ((EditText) findViewById(R.id.title)).getText().toString()));
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
                    String new_title = ((EditText) findViewById(R.id.title)).getText().toString();
                    Intent intent = new Intent();
                    intent.putExtra("title", new_title);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else
                {
                    Log.d("NoteDetailActivity", "Error: " + response.message());
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED, intent);
                    Toasty.error(NoteDetailActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT, true).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<AddFileResponse> call, Throwable t) {
                // 请求失败
                Log.d("NoteDetailActivity", "Error: " + t.getMessage());
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                Toasty.error(NoteDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT, true).show();
                finish();

            }
        });
    }

    private void expandAIWindow() {
        BottomDialog.show("AI对话", "",
            new OnBindView<BottomDialog>(R.layout.dialog_ai) {
                @Override
                public void onBind(BottomDialog dialog, View v) {
                    state = DIALOG_STATE.INIT;
                    dialog_ai = v;
                }
            })
            .setOtherButton("发送", new OnDialogButtonClickListener<BottomDialog>() {
                @Override
                public boolean onClick(BottomDialog baseDialog, View v) {
                    assert state != DIALOG_STATE.CLOSE;
                    // 判断状态，获取输入
                    if (state == DIALOG_STATE.WAIT_FOR_ANSWER) {
                        Toast.makeText(getApplicationContext(),
                                "小术正在上一个问题，请稍等~",
                                Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    EditText editText = dialog_ai.findViewById(R.id.prompt);
                    String question = editText.getText().toString();
                    if (question.isEmpty()) {
                        Toast.makeText(getApplicationContext(),
                                "请输入您的问题！",
                                Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    // 设置界面，更新状态
                    TextView answer = dialog_ai.findViewById(R.id.answer);
                    answer.setText("小术已经收到了您的问题，请稍作等待~");
                    answer.setTextColor(ContextCompat.getColor(getApplicationContext(),
                            com.kongzue.dialogx.R.color.black40));
                    state = DIALOG_STATE.WAIT_FOR_ANSWER;

                    // 发送请求，获取AI对话结果
                    API api = API.Creator.createApiService();
                    Call<AIChatResponse> call = api.chatWithAI(new AIChatRequest(question, noteList));
                    call.enqueue(new Callback<AIChatResponse>() {
                        @Override
                        public void onResponse(Call<AIChatResponse> call, Response<AIChatResponse> response) {
                            if(state != DIALOG_STATE.WAIT_FOR_ANSWER) {
                                return;
                            }
                            // 请求成功，记录答案并
                            AIChatResponse aiResponse = response.body();
                            TextView answer = dialog_ai.findViewById(R.id.answer);
                            answer.setText(aiResponse.getAnswer());
                            answer.setTextColor(ContextCompat.getColor(getApplicationContext(),
                                    com.kongzue.dialogx.R.color.black));
                            state = DIALOG_STATE.OBTAINED_ANSWER;
                        }

                        @Override
                        public void onFailure(Call<AIChatResponse> call, Throwable t) {
                            // 请求失败
                            Log.d("TestActivity", "Error: " + t.getMessage());
                            Toasty.error(NoteDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT, true).show();
                            answer.setText("小术AI为您服务~");
                            answer.setTextColor(ContextCompat.getColor(getApplicationContext(),
                                    com.kongzue.dialogx.R.color.black40));
                            state = DIALOG_STATE.INIT;
                        }
                    });

                    return true;
                }
            })
            .setCancelButton("复制回答", new OnDialogButtonClickListener<BottomDialog>() {
                @Override
                public boolean onClick(BottomDialog baseDialog, View v) {
                    assert state != DIALOG_STATE.CLOSE;
                    if (state == DIALOG_STATE.INIT) {
                        Toast.makeText(getApplicationContext(),
                                "请输入您的问题并发送~",
                                Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    else if (state == DIALOG_STATE.WAIT_FOR_ANSWER) {
                        Toast.makeText(getApplicationContext(),
                                "小术还在思考如何回答，请您稍等~",
                                Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    // 复制问题到剪切板
                    ClipboardManager manager = (ClipboardManager) getApplicationContext()
                                                .getSystemService(Context.CLIPBOARD_SERVICE);
                    TextView answer = dialog_ai.findViewById(R.id.answer);
                    ClipData answerData = ClipData.newPlainText("Answering", answer.getText().toString());
                    manager.setPrimaryClip(answerData);
//                        Toast.makeText(getApplicationContext(), "已复制回答到剪贴板",
//                                Toast.LENGTH_SHORT).show();
                    return false;
                }
            })
            .setOkButton("取消", new OnDialogButtonClickListener<BottomDialog>() {
                @Override
                public boolean onClick(BottomDialog baseDialog, View v) {
                    state = DIALOG_STATE.CLOSE;
                    return false;
                }
            });
    }
}
