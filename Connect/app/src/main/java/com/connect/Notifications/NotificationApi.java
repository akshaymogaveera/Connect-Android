package com.connect.Notifications;

import com.connect.Home.HomeActivity;
import com.connect.Notifications.models.Notification;
import com.connect.main.R;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Query;

public interface NotificationApi {

    String BASE_URL="http://"+ HomeActivity.getContext().getResources().getString(R.string.ip)+":8000/firstapp/";


//    @GET("post/latest/like/")
//    Observable<List<Notification>> getLikesNotifications(
//            @HeaderMap Map<String, String> headers
//    );
//
//    @GET("post/latest/comments/")
//    Observable<List<Notification>> getCommentsNotifications(
//            @HeaderMap Map<String, String> headers
//    );

    @GET("post/latest/like/")
    Observable<List<Notification>> getLikesNotifications(
            @HeaderMap Map<String, String> headers,
            @Query("page") int page
    );

    @GET("post/latest/comments/")
    Observable<List<Notification>> getCommentsNotifications(
            @HeaderMap Map<String, String> headers,
             @Query("page") int page
    );


    public static Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());

    static Retrofit retrofit = retrofitBuilder.build();

    static NotificationApi requestApi = retrofit.create(NotificationApi.class);

    public static NotificationApi getRequestApi(){
        return requestApi;
    }
}
