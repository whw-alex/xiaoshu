package com.example.xiaoshu;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import es.dmoral.toasty.Toasty;
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
import android.content.SharedPreferences;

import com.example.xiaoshu.Request.ResetPasswordRequest;
import com.example.xiaoshu.Response.ResetPasswordResponse;
import com.google.android.material.appbar.MaterialToolbar;

public class UserResetActivity extends AppCompatActivity{
    TextView old_password;
    TextView new_password;
    TextView confirm_password;
    Button save;
    Button cancel;
    MaterialToolbar topAppBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_reset);
        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> {
            finish();
        });
        old_password = findViewById(R.id.old_password);
        new_password = findViewById(R.id.new_password);
        confirm_password = findViewById(R.id.confirm_password);
        save = findViewById(R.id.btn_save);
        cancel = findViewById(R.id.btn_cancel);

        save.setOnClickListener(v -> {
                // save changes
            if (isValidateInput()) {
                API api = API.Creator.createApiService();
                SharedPreferences sharedPreferences = getSharedPreferences("login_status", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(sharedPreferences.getInt("id", 0), old_password.getText().toString(), new_password.getText().toString());

                Call<ResetPasswordResponse> call = api.resetPassword(resetPasswordRequest);
                call.enqueue(new Callback<ResetPasswordResponse>() {
                    @Override
                    public void onResponse(Call<ResetPasswordResponse> call, Response<ResetPasswordResponse> response) {
                        if(response.isSuccessful())
                        {
                            Toasty.success(getApplicationContext(), "Password reset successfully", Toast.LENGTH_SHORT, true).show();
                            finish();
                        }
                        else
                        {
                            Toasty.error(getApplicationContext(), "Password reset failed", Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                        Toasty.error(getApplicationContext(), "Password reset failed", Toast.LENGTH_SHORT, true).show();
                    }
                });
                }

            });


    }

    public boolean isValidateInput()
    {
        if(old_password.getText().toString().isEmpty())
        {
            old_password.setError("Password cannot be empty");
            return false;
        }
        if(new_password.getText().toString().isEmpty())
        {
            new_password.setError("Confirm password cannot be empty");
            return false;
        }
        if(old_password.getText().toString().equals(new_password.getText().toString()))
        {
            new_password.setError("New password must be different from old password");
            return false;
        }
        if(!new_password.getText().toString().equals(confirm_password.getText().toString()))
        {
            confirm_password.setError("Password and confirm password must be the same");
            return false;
        }
        return true;
    }

}
