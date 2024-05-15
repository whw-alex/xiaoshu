package com.example.xiaoshu;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import es.dmoral.toasty.Toasty;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
public class UserResetActivity extends AppCompatActivity{
    ImageView avatar_;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_reset);
    }

}
