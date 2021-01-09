package com.connect.Comments;

import com.connect.Comments.model.Comments;
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

public interface CommentsApi {

    //String BASE_URL = "http://192.168.42.206:8000/firstapp/";
    String BASE_URL="http://"+ HomeActivity.getContext().getResources().getString(R.string.ip)+":8000/firstapp/";


    @Headers("Content-Type: application/json")
    @POST("user/profile/pic/")
    Observable<ResponseBody> getProfilePic(
            @Body Map<String, String> body,
            @HeaderMap Map<String, String> headers
    );

//    @Headers("Content-Type: application/json")
//    @POST("post/comments/list/")
//    Observable<List<Comments>> getCommentsList(
//            @Body Map<String, String> body,
//            @HeaderMap Map<String, String> headers
//    );

    @Headers("Content-Type: application/json")
    @GET("post/{id}/comments/list/")
    Observable<List<Comments>> getCommentsList(
            @HeaderMap Map<String, String> headers,
            @Path("id") int id,
            @Query("page") int page
    );

    @Headers("Content-Type: application/json")
    @POST("post/comment/")
    Call<Comments> addComment(
            @Body Map<String, String> body,
            @HeaderMap Map<String, String> headers
    );



    public static Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());

    static Retrofit retrofit = retrofitBuilder.build();

    static CommentsApi requestApi = retrofit.create(CommentsApi.class);

    public static CommentsApi getRequestApi(){
        return requestApi;
    }
}
