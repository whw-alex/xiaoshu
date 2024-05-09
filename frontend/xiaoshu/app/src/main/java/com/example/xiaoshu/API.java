package com.example.xiaoshu;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import com.example.xiaoshu.Request.UserRequest;
import com.example.xiaoshu.Response.UserResponse;
import com.example.xiaoshu.Response.LoginResponse;
import com.example.xiaoshu.Request.LoginRequest;

public interface API {
    @POST("user/register/")
    Call<UserResponse> registerUser(@Body UserRequest userRequest);

    @POST("user/login/")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    class Creator {
        public static API createApiService() {
            return RetrofitClient.getInstance().create(API.class);
        }
    }

}
