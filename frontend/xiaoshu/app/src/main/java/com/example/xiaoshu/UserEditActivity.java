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

public class UserEditActivity extends AppCompatActivity{
    ImageView avatar_;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_edit);
        avatar_ = findViewById(R.id.avatar);
        avatar_.setOnClickListener(v -> {
            // select image
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);


        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // 获取选定的图片
            Uri imageUri = data.getData();

            // 将图片设置到 ImageView
            avatar_.setImageURI(imageUri);
        }
    }
}
