package com.example.xiaoshu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.xiaoshu.API;
import com.example.xiaoshu.Request.UserRequest;
import com.example.xiaoshu.Response.UserResponse;

import java.util.logging.Logger;

public class RegisterActivity extends AppCompatActivity {
    private Button registerBtn, cancelBtn;
    EditText username, password, confirmPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        registerBtn = findViewById(R.id.btn_register);
        cancelBtn = findViewById(R.id.btn_cancel);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        // set on click listener for cancel button
        cancelBtn.setOnClickListener(v -> {
            // goto login activity
            finish();
        });
        // set on click listener for register button
        registerBtn.setOnClickListener(v -> {
            if(validateInput())
            {
                // TODO: send request to server
                registerUser();
            }
        });
    }

    private void registerUser() {
        // get username and password
        String usernameStr = username.getText().toString();
        String passwordStr = password.getText().toString();
        UserRequest userRequest = new UserRequest(usernameStr, passwordStr, "");

        // send request to server
        API apiService = API.Creator.createApiService();
        Call<UserResponse> call = apiService.registerUser(userRequest);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
//                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    Toasty.success(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT, true).show();
                    // goto login activity
                    finish();
                } else {
//                    Toast.makeText(RegisterActivity.this, "Registration failed: A user with that username already exists.", Toast.LENGTH_SHORT).show();
                    Toasty.error(RegisterActivity.this, "Registration failed: A user with that username already exists.", Toast.LENGTH_SHORT, true).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
//                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Toasty.error(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });

    }

    private boolean validateInput() {
        // validate username and password
        String usernameStr = username.getText().toString();
        String passwordStr = password.getText().toString();
        String confirmPasswordStr = confirmPassword.getText().toString();
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
        if(confirmPasswordStr.isEmpty())
        {
            confirmPassword.setError("Confirm password cannot be empty");
            return false;
        }
        if(!passwordStr.equals(confirmPasswordStr))
        {
            confirmPassword.setError("Password and confirm password must be the same");
            return false;
        }
        // password must be at least 8 characters
//        if(passwordStr.length() < 8)
//        {
//            password.setError("Password must be at least 8 characters");
//            return false;
//        }
        return true;
    }

}