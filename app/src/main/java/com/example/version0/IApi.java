package com.example.version0;


import com.example.version0.UploadResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface IApi {

    //TODO 4
    // 补全所有注解
    @Multipart
    @POST("video")
    Call<UploadResponse> submitMessage(@Query("student_id") String student_id,
                                       @Query("extra_value") String extra_value,
                                       @Query("user_name") String user_name,
                                       @Part MultipartBody.Part cover_image,
                                       @Part MultipartBody.Part video,
                                       @Header("token") String token);
}
