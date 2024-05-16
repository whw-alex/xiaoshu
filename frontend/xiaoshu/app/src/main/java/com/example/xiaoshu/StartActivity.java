package com.example.xiaoshu;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import es.dmoral.toasty.Toasty;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import com.example.xiaoshu.Request.LoginRequest;
import com.example.xiaoshu.Response.LoginResponse;
import com.example.xiaoshu.RegisterActivity;

import android.content.SharedPreferences;



public class StartActivity extends AppCompatActivity{

    private Button registerBtn, loginBtn;
    EditText username, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        registerBtn = findViewById(R.id.register_button);
        loginBtn = findViewById(R.id.login_button);
        username = findViewById(R.id.username_edit_text);
        password = findViewById(R.id.password_edit_text);
        // set on click listener for login button
        loginBtn.setOnClickListener(v -> {
            // get username and password
            String usernameStr = username.getText().toString();
            String passwordStr = password.getText().toString();
            // validate input
            if(validateInput())
            {
                login();
            }

        });
        registerBtn.setOnClickListener(v -> {
            // goto register activity
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
        // check if user is already logged in
//        SharedPreferences sharedPreferences = getSharedPreferences("login_status", MODE_PRIVATE);
//        String token = sharedPreferences.getString("token", "");
//        String id = sharedPreferences.getString("id", "");
//        if(!token.isEmpty())
//        {
//            ApiService apiService = ApiService.Creator.createApiService();
//            Call<Void> call = apiService.testAuthorization(token);
//            call.enqueue(new Callback<Void>() {
//                @Override
//                public void onResponse(Call<Void> call, Response<Void> response) {
//                    // if token is valid
//                    if(response.isSuccessful())
//                    {
//                        startService();
//                        gotoContentActivity();
//                    }
//                    else
//                    {
//                        // if token is invalid
//                        Toasty.error(MainActivity.this, "Token has expired!", Toast.LENGTH_SHORT, true).show();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Void> call, Throwable t) {
//                }
//            });
//        }
    }

    private boolean validateInput() {
        // validate username and password
        String usernameStr = username.getText().toString();
        String passwordStr = password.getText().toString();
        if(usernameStr.isEmpty())
        {
            username.setError("Username cannot be empty");
            return false;
        }
        if(passwordStr.isEmpty())
        {
            password.setError("Password cannot be empty");
            return false;
        }
        return true;
    }
    private void login()
    {
        String usernameStr = username.getText().toString();
        String passwordStr = password.getText().toString();
        LoginRequest loginRequest = new LoginRequest(usernameStr, passwordStr);
        // send request to server
        API api = API.Creator.createApiService();
        Call<LoginResponse> call = api.loginUser(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    // print response body
                    LoginResponse loginResponse = response.body();
                    System.out.println(loginResponse);
                    Toasty.success(StartActivity.this, "Login successful!", Toast.LENGTH_SHORT, true).show();
                    // save token to shared preferences
                    SharedPreferences sharedPreferences = getSharedPreferences("login_status", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("signature", loginResponse.getSignature());
                    editor.putInt("id", loginResponse.getId());
                    editor.putString("username", loginResponse.getUsername());
                    editor.putString("avatar", loginResponse.getAvatar());
                    editor.apply();
                    Log.d("login", loginResponse.getAvatar());
//                    startService();
                    gotoMainActivity();

                }
                else
                {
                    Toasty.error(StartActivity.this, "Username or password is not correct!", Toast.LENGTH_SHORT, true).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toasty.error(StartActivity.this, "Login failed!", Toast.LENGTH_SHORT, true).show();
            }
        });

    }
    private void gotoMainActivity()
    {
        Log.d("StartActivity", "gotoMainActivity: ");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
