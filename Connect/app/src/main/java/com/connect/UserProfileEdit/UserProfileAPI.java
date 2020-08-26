package com.connect.UserProfile;

import com.connect.UserProfile.models.Info;
import com.connect.UserProfile.models.UserProfile;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;


public interface UserProfileAPI {


    @Headers("Content-Type: application/json")
    @HTTP(method = "POST", path = "user/profile/info/", hasBody = true)
    Call<UserProfile> getData(
            @Body Map<String, String> body,
            @HeaderMap Map<String, String> headers
    );

    @Multipart
    @POST("register/update/")
    Call<Info> saveData(
            @Part MultipartBody.Part profile_pic,
            @Part("email") RequestBody email,
            @Part("city") RequestBody city,
            @Part("country") RequestBody country,
            @HeaderMap Map<String, String> headers
    );

    @Multipart
    @POST("register/update/")
    Call<Info> saveDataWithoutProfilePic(
            @Part("email") RequestBody email,
            @Part("city") RequestBody city,
            @Part("country") RequestBody country,
            @HeaderMap Map<String, String> headers
    );

    @Multipart
    @POST("register/update/")
    Call<Info> saveProfileWithProfilePic(
            @Part MultipartBody.Part profile_pic,
            @PartMap() Map<String, RequestBody> partMap,
            @HeaderMap Map<String, String> headers
    );

    @Multipart
    @POST("register/update/")
    Call<Info> saveProfileWithoutProfilePic(
            @PartMap() Map<String, RequestBody> partMap,
            @HeaderMap Map<String, String> headers
    );
}