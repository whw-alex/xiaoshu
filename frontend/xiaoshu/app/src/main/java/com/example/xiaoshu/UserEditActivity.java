package com.example.xiaoshu;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import es.dmoral.toasty.Toasty;

import android.content.SharedPreferences;
import com.example.xiaoshu.Request.ProfileRequest;
import com.example.xiaoshu.Response.ProfileResponse;
import com.example.xiaoshu.API;

import android.content.SharedPreferences;
import android.os.Bundle;
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

public class UserEditActivity extends AppCompatActivity{
    ImageView avatar_;
    String temp_avatar_url;
    Button save;
    Button cancel;
    TextView username_;
    TextView signature_;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_edit);
        username_ = findViewById(R.id.username);
        signature_ = findViewById(R.id.signature);
        avatar_ = findViewById(R.id.avatar);
        avatar_.setOnClickListener(v -> {
            // select image
            Log.d("UserEditActivity", "onCreate: avatar_ clicked");
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            startActivityForResult(intent, 1);


        });
        save = findViewById(R.id.btn_save);
        save.setOnClickListener(v -> {
            // save changes
            Log.d("UserEditActivity", "onCreate: save clicked");
            API api = API.Creator.createApiService();
            SharedPreferences sharedPreferences = getSharedPreferences("login_status", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Call<ProfileResponse> call = api.updateProfile(new ProfileRequest(username_.getText().toString(), signature_.getText().toString(), temp_avatar_url, sharedPreferences.getInt("id", 0)));
            call.enqueue(new Callback<ProfileResponse>() {
                @Override
                public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                    if(response.isSuccessful())
                    {
                        Log.d("UserEditActivity", "onResponse: " + response.body().toString());
                        editor.putString("avatar", temp_avatar_url);
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
            temp_avatar_url = imageUri.toString();


        }
    }
}
