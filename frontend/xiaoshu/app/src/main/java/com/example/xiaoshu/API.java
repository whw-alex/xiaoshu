package com.example.xiaoshu;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import com.example.xiaoshu.Request.FileListRequest;
import com.example.xiaoshu.Request.UserRequest;
import com.example.xiaoshu.Request.AddFileRequest;
import com.example.xiaoshu.Response.AddFileResponse;
import com.example.xiaoshu.Response.FilelistResponse;
import com.example.xiaoshu.Response.UserResponse;
import com.example.xiaoshu.Response.LoginResponse;
import com.example.xiaoshu.Request.LoginRequest;
import com.example.xiaoshu.Request.NoteDetailRequest;
import com.example.xiaoshu.Response.NoteInfoResponse;
import com.example.xiaoshu.Response.NoteItemResponse;
import com.example.xiaoshu.Request.ProfileRequest;
import com.example.xiaoshu.Response.ProfileResponse;
import com.example.xiaoshu.Request.ResetPasswordRequest;
import com.example.xiaoshu.Response.ResetPasswordResponse;
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

    @POST("user/profile/")
    Call<ProfileResponse> updateProfile(@Body ProfileRequest profileRequest);

    @POST("user/reset_password/")
    Call<ResetPasswordResponse> resetPassword(@Body ResetPasswordRequest resetPasswordRequest);

    @POST("user/file_list/")
    Call<FilelistResponse> get_file_list(@Body FileListRequest fileListRequest);

    @POST("user/create_file/")
    Call<AddFileResponse> create_file(@Body AddFileRequest fileListRequest);

    class Creator {
        public static API createApiService() {
            return RetrofitClient.getInstance().create(API.class);
        }
    }

}
