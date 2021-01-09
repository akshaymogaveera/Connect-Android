package com.connect.NewsFeed;

import com.connect.Home.HomeActivity;
import com.connect.NewsFeed.model.Feed;
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

public interface NewsFeedApi {

    //String BASE_URL = "http://192.168.42.206:8000/firstapp/";
    String BASE_URL="http://"+ HomeActivity.getContext().getResources().getString(R.string.ip)+":8000/firstapp/";
//
//    int cacheSize = 10 * 1024 * 1024;
//    Cache cache = Validate.cache;
//    OkHttpClient client = new OkHttpClient.Builder()
//            .cache(cache)
//            .build();


    @Headers("Content-Type: application/json")
    @GET("newsfeed/")
    Call<List<Feed>> getData(
            @HeaderMap Map<String, String> headers
    );

    @Headers("Content-Type: application/json")
    @GET("newsfeed/pagination/")
    Call<List<Feed>> getData1(
            @HeaderMap Map<String, String> headers,
            @Query("page") int page
    );

    @Headers("Content-Type: application/json")
    @POST("post/likes/count/")
    Call<ResponseBody> countLikes(
            @Body Map<String, String> body,
            @HeaderMap Map<String, String> headers
    );

//    @Headers("Content-Type: application/json")
//    @GET("newsfeed/")
//    Observable<List<Feed>> getObsData(
//            @HeaderMap Map<String, String> headers
//    );


    @Headers("Content-Type: application/json")
    @GET("newsfeed/pagination/")
    Observable<List<Feed>> getObsData(
            @HeaderMap Map<String, String> headers,
            @Query("page") int page
    );


//    @Headers("Content-Type: application/json")
//    @POST("user/feed/")
//    Observable<List<Feed>> getUserFeed(
//            @Body Map<String, String> body,
//            @HeaderMap Map<String, String> headers
//    );

    @Headers("Content-Type: application/json")
    @GET("user/{id}/feed/")
    Observable<List<Feed>> getUserFeed(
            @HeaderMap Map<String, String> headers,
            @Path("id") int id,
            @Query("page") int page
    );

    @Headers("Content-Type: application/json")
    @POST("post/likes/count/")
    Observable<ResponseBody> countLikesObs(
            @Body Map<String, String> body,
            @HeaderMap Map<String, String> headers
    );

    @Headers("Content-Type: application/json")
    @POST("post/comments/count/")
    Observable<ResponseBody> countCommentsObs(
            @Body Map<String, String> body,
            @HeaderMap Map<String, String> headers
    );

    @Headers("Content-Type: application/json")
    @POST("post/liked/")
    Observable<ResponseBody> getLiked(
            @Body Map<String, String> body,
            @HeaderMap Map<String, String> headers
    );

    public static Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());

    static Retrofit retrofit = retrofitBuilder.build();

    static NewsFeedApi requestApi = retrofit.create(NewsFeedApi.class);

    public static NewsFeedApi getRequestApi(){
        return requestApi;
    }


}



