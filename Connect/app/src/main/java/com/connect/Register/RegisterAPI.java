package com.connect.Register;

import com.connect.UserProfileEdit.models.Info;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;


public interface RegisterAPI {


    @Multipart
    @POST("register/")
    Call<Info> saveProfileWithProfilePic(
            @Part MultipartBody.Part profile_pic,
            @PartMap() Map<String, RequestBody> partMap
    );


}