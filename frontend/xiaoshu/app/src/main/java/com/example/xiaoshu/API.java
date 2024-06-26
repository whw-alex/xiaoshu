package com.example.xiaoshu;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import com.example.xiaoshu.Request.AIChatRequest;
import com.example.xiaoshu.Request.DeleteFileRequest;
import com.example.xiaoshu.Request.FileListRequest;
import com.example.xiaoshu.Request.SearchRequest;
import com.example.xiaoshu.Request.UserRequest;
import com.example.xiaoshu.Request.AddFileRequest;
import com.example.xiaoshu.Response.AIChatResponse;
import com.example.xiaoshu.Response.AddFileResponse;
import com.example.xiaoshu.Response.DeleteFileResponse;
import com.example.xiaoshu.Response.FilelistResponse;
import com.example.xiaoshu.Response.SearchResponse;
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
import com.example.xiaoshu.Request.SaveNoteTestRequest;
import com.example.xiaoshu.Request.UploadFakeImageRequest;
import com.example.xiaoshu.Request.DeleteItemRequest;
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

    @POST("user/search/")
    Call<SearchResponse> get_search_result(@Body SearchRequest fileListRequest);

    @POST("user/create_file/")
    Call<AddFileResponse> create_file(@Body AddFileRequest fileListRequest);

    @POST("user/delete_file/")
    Call<DeleteFileResponse> delete_file(@Body DeleteFileRequest fileListRequest);

    @POST("user/delete_item/")
    Call<DeleteFileResponse> delete_item(@Body DeleteItemRequest fileListRequest);

    @POST("user/upload_note_image/")
    Call<AddFileResponse> uploadImage(@Body RequestBody photo);

    @POST("user/upload_note_fake_image/")
    Call<AddFileResponse> uploadFakeImage(@Body UploadFakeImageRequest request);

    @POST("user/upload_profile_image/")
    Call<AddFileResponse> uploadProfileImage(@Body RequestBody photo);

    @POST("user/upload_note_audio/")
    Call<AddFileResponse> uploadAudio(@Body RequestBody audio);

    @POST("user/save_note_text/")
    Call<AddFileResponse> saveNoteText(@Body SaveNoteTestRequest saveNoteTestRequest);

    @POST("user/chat/")
    Call<AIChatResponse> chatWithAI(@Body AIChatRequest AIChatRequest);

    class Creator {
        public static API createApiService() {
            return RetrofitClient.getInstance().create(API.class);
        }
    }

}
