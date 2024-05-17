package com.example.xiaoshu;

import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.*;
import androidx.recyclerview.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.net.Uri;


import com.example.xiaoshu.R;
import android.content.Intent;
import android.os.Bundle;
import com.example.xiaoshu.UserEditActivity;
import com.example.xiaoshu.UserResetActivity;

import java.util.*;

public class UserFragment extends Fragment{
    ImageView imageView_edit;
    ImageView imageView_settings;
    ImageView imageView_logout;
    ImageView avatar_;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        imageView_edit = view.findViewById(R.id.edit);
        imageView_edit.setOnClickListener(v -> {
            // goto user edit activity
                Intent intent = new Intent(requireContext(), UserEditActivity.class);
                startActivity(intent);

        });
        imageView_settings = view.findViewById(R.id.settings);
        imageView_settings.setOnClickListener(v -> {
            // goto user edit activity
                Intent intent = new Intent(requireContext(), UserResetActivity.class);
                startActivity(intent);

        });
        imageView_logout = view.findViewById(R.id.logout);
        imageView_logout.setOnClickListener(v -> {
            // logout
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("login_status", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("login", false);
            editor.apply();
            Intent intent = new Intent(requireContext(), StartActivity.class);
            startActivity(intent);
        });

        avatar_ = view.findViewById(R.id.avatar_);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("login_status", Context.MODE_PRIVATE);
        String avatar_url = sharedPreferences.getString("avatar", "");
        Log.d("UserFragment", "onCreateView: avatar_url: " + avatar_url);
        String username = sharedPreferences.getString("username", "");
        TextView username_ = view.findViewById(R.id.username_);
        username_.setText(username);
        String signature = sharedPreferences.getString("signature", "");
        TextView signature_ = view.findViewById(R.id.signature_);
        signature_.setText(signature);

        if(!avatar_url.isEmpty())
        {
            avatar_.setImageURI(null);
            avatar_.setImageURI(Uri.parse(avatar_url));
        }
        else
        {
            avatar_.setImageResource(R.drawable.avatar_11);
        }

        return view;
    }

    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("login_status", Context.MODE_PRIVATE);
        String avatar_url = sharedPreferences.getString("avatar", "");
        Log.d("UserFragment", "onCreateView: avatar_url: " + avatar_url);
        String username = sharedPreferences.getString("username", "");
        TextView username_ = getView().findViewById(R.id.username_);
        username_.setText(username);
        String signature = sharedPreferences.getString("signature", "");
        TextView signature_ = getView().findViewById(R.id.signature_);
        signature_.setText(signature);

        if(!avatar_url.isEmpty())
        {
            Uri avatar_uri = Uri.parse(avatar_url);
            Log.d("UserFragment", "onCreateView: avatar_uri: " + avatar_uri);
            avatar_.setImageURI(avatar_uri);
        }
        else
        {
            avatar_.setImageResource(R.drawable.avatar_11);
        }
    }



}
