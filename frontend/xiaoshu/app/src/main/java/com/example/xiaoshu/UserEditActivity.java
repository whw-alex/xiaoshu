package com.example.xiaoshu;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import es.dmoral.toasty.Toasty;

import android.content.SharedPreferences;
import com.example.xiaoshu.Request.ProfileRequest;
import com.example.xiaoshu.Response.AddFileResponse;
import com.example.xiaoshu.Response.ProfileResponse;
import com.example.xiaoshu.API;
import com.google.android.material.appbar.MaterialToolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UserEditActivity extends AppCompatActivity{
    ImageView avatar_;
    Uri avatar_uri;
    Button save;
    Button cancel;
    TextView username_;
    TextView signature_;
    MaterialToolbar topAppBar;
    private static final int REQUEST_IMAGE_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_edit);
        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> {
            finish();
        });
        username_ = findViewById(R.id.username);
        signature_ = findViewById(R.id.signature);
        avatar_ = findViewById(R.id.avatar);
        SharedPreferences sharedPreferences = getSharedPreferences("login_status", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String avatar = sharedPreferences.getString("avatar", "");
        if (avatar != null && !avatar.equals("")) {
            Uri uri = Uri.parse(avatar);
            avatar_.setImageURI(uri);
        }
        else {
            avatar_.setImageResource(R.drawable.avatar_11);
        }
        username_.setText(sharedPreferences.getString("username", ""));
        signature_.setText(sharedPreferences.getString("signature", ""));

        avatar_.setOnClickListener(v -> {
            // select image
            Log.d("UserEditActivity", "onCreate: avatar_ clicked");
//            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.setType("image/*");
//            startActivityForResult(intent, 1);
            pickImageFromGallery();



        });
        save = findViewById(R.id.btn_save);
        save.setOnClickListener(v -> {
            // save changes
            Log.d("UserEditActivity", "onCreate: save clicked");
            uploadImage(avatar_uri);
            API api = API.Creator.createApiService();
//            SharedPreferences sharedPreferences = getSharedPreferences("login_status", MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
            Call<ProfileResponse> call = api.updateProfile(new ProfileRequest(username_.getText().toString(), signature_.getText().toString(), "", sharedPreferences.getInt("id", 0)));
            call.enqueue(new Callback<ProfileResponse>() {
                @Override
                public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                    if(response.isSuccessful())
                    {
                        Log.d("UserEditActivity", "onResponse: " + response.body().toString());
                        editor.putString("avatar", avatar_uri.toString());
                        editor.putString("username", username_.getText().toString());
                        editor.putString("signature", signature_.getText().toString());
                        editor.apply();
                        Toasty.success(UserEditActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        Log.d("UserEditActivity", "onResponse: " + response.errorBody().toString());
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<ProfileResponse> call, Throwable t) {
                    Log.d("UserEditActivity", "onFailure: " + t.getMessage());
                    finish();
                }
            });


        });
        cancel = findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(v -> {
            // cancel changes
            Log.d("UserEditActivity", "onCreate: cancel clicked");
            finish();
        });

    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("UserEditActivity", "onActivityResult: " + requestCode + " " + resultCode + " " + data.toString());
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // 获取选定的图片
            Uri imageUri = data.getData();
            Log.d("UserEditActivity", "onActivityResult: " + imageUri.toString());
            // 将图片设置到 ImageView
            avatar_.setImageURI(imageUri);
            avatar_uri = imageUri;
//            uploadImage(imageUri);


        }
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
            RequestBody body = builder.build();
            Log.d("NoteDetailActivity", "uploadImage: " + body.contentType() + " " + body.contentLength());
            Call<AddFileResponse> call = api.uploadProfileImage(body);
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
                    Toasty.error(UserEditActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT, true).show();
                }
            });
            os.close();
            is.close();
        } catch (IOException e) {
            Log.d("NoteDetailActivity", "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
