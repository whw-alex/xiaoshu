package com.example.xiaoshu;

import androidx.fragment.app.Fragment;
import android.content.Context;
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


import com.example.xiaoshu.R;
import android.content.Intent;
import android.os.Bundle;
import com.example.xiaoshu.UserEditActivity;
import com.example.xiaoshu.UserResetActivity;

import java.util.*;

public class UserFragment extends Fragment{
    ImageView imageView_edit;
    ImageView imageView_settings;
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
        return view;
    }


}
