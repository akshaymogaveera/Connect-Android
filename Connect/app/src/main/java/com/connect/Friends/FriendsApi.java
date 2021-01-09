package com.connect.Friends;

import com.connect.Friends.model.Friend;
import com.connect.Friends.model.User2;
import com.connect.Home.HomeActivity;
import com.connect.main.R;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FriendsApi {

    //String BASE_URL = "http://192.168.42.206:8000/firstapp/";
    String BASE_URL="http://"+ HomeActivity.getContext().getResources().getString(R.string.ip)+":8000/firstapp/";

    @GET("friend/count/")
    Call<ResponseBody> getFriendsCount(
            @HeaderMap Map<String, String> headers
    );

    @Headers("Content-Type: application/json")
    @POST("friend/count/")
    Call<ResponseBody> getUsersFriendsCount(
            @Body Map<String, String> body,
            @HeaderMap Map<String, String> headers
    );

    @Headers("Content-Type: application/json")
    @POST("friend/status/")
    Call<ResponseBody> getFriendStatus(
            @Body Map<String, String> body,
            @HeaderMap Map<String, String> headers
    );


    @Headers("Content-Type: application/json")
    @POST("friend/edit/")
    Call<ResponseBody> addFriend(
            @Body Map<String, String> body,
            @HeaderMap Map<String, String> headers
    );

    @Headers("Content-Type: application/json")
    @POST("friend/edit/")
    Call<ResponseBody> deleteFriend(
            @Body Map<String, String> body,
            @HeaderMap Map<String, String> headers
    );

    @Headers("Content-Type: application/json")
    @POST("friend/mutual/count/")
    Call<ResponseBody> getMutualFriends(
            @Body Map<String, String> body,
            @HeaderMap Map<String, String> headers
    );


//    @GET("friend/request/")
//    Observable<List<Friend>> getFriendRequestList(
//            @HeaderMap Map<String, String> headers
//    );

    @GET("friend/request/")
    Observable<List<Friend>> getFriendRequestList(
            @HeaderMap Map<String, String> headers,
            @Query("page") int page
    );

//    @Headers("Content-Type: application/json")
//    @POST("friend/list/")
//    Observable<List<Friend>> getFriendList(
//            @Body Map<String, String> body,
//            @HeaderMap Map<String, String> headers
//    );

    @Headers("Content-Type: application/json")
    @GET("friend/{id}/list/")
    Observable<List<Friend>> getFriendList(
            @HeaderMap Map<String, String> headers,
            @Path("id") int id,
            @Query("page") int page
    );

//    @Headers("Content-Type: application/json")
//    @POST("friend/mutual/list/")
//    Observable<List<User2>> getMutualFriendList(
//            @Body Map<String, String> body,
//            @HeaderMap Map<String, String> headers
//    );

    @Headers("Content-Type: application/json")
    @GET("friend/{id}/mutual/list/")
    Observable<List<User2>> getMutualFriendList(
            @HeaderMap Map<String, String> headers,
            @Path("id") int id,
            @Query("page") int page
    );


    public static Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());

    static Retrofit retrofit = retrofitBuilder.build();

    static FriendsApi requestApi = retrofit.create(FriendsApi.class);

    public static FriendsApi getRequestApi(){
        return requestApi;
    }
}
