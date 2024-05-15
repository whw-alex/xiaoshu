package com.example.xiaoshu;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import com.example.xiaoshu.Request.UserRequest;
import com.example.xiaoshu.Response.UserResponse;
import com.example.xiaoshu.Response.LoginResponse;
import com.example.xiaoshu.Request.LoginRequest;
import com.example.xiaoshu.Request.NoteDetailRequest;
import com.example.xiaoshu.Response.NoteInfoResponse;
import com.example.xiaoshu.Response.NoteItemResponse;
import java.util.List;

public interface API {
    @POST("user/register/")
    Call<UserResponse> registerUser(@Body UserRequest userRequest);

    @POST("user/login/")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

//    @POST("user/folder/")

    @POST("user/note_info/")
    Call<NoteInfoResponse> noteInfo(@Body NoteDetailRequest notedetailRequest);
    @POST("user/note_list/")
    Call<List<NoteItemResponse>> noteDetail(@Body NoteDetailRequest notedetailRequest);



    class Creator {
        public static API createApiService() {
            return RetrofitClient.getInstance().create(API.class);
        }
    }

}
