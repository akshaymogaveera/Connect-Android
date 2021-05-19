package com.connect.Post;

import com.connect.Home.HomeActivity;
import com.connect.main.R;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface PostApi {

    //String BASE_URL="http://"+ HomeActivity.getContext().getResources().getString(R.string.ip)+":8000/firstapp/";
    String BASE_URL="https://"+ HomeActivity.getContext().getResources().getString(R.string.ip)+"/firstapp/";

    @Multipart
    @POST("post/")
    Call<ResponseBody> CreatePostWithoutImage(
            @Body Map<String, String> body,
            @HeaderMap Map<String, String> headers
    );

    @Multipart
    @POST("post/")
    Call<ResponseBody> createPostWithImage(
            @Part MultipartBody.Part post_pic,
            @PartMap() Map<String, RequestBody> partMap,
            @HeaderMap Map<String, String> headers
    );

    @Headers("Content-Type: application/json")
    @POST("post/delete/")
    Call<ResponseBody> deletePost(
            @Body Map<String, String> body,
            @HeaderMap Map<String, String> headers
    );


    @GET("post/count/")
    Call<ResponseBody> getPostCount(
            @HeaderMap Map<String, String> headers
    );

    @Headers("Content-Type: application/json")
    @POST("post/count/")
    Call<ResponseBody> getUsersPostCount(
            @Body Map<String, String> body,
            @HeaderMap Map<String, String> headers
    );


    public static Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());

    static Retrofit retrofit = retrofitBuilder.build();

    static PostApi requestApi = retrofit.create(PostApi.class);

    public static PostApi getRequestApi(){
        return requestApi;
    }
}
