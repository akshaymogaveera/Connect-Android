package com.connect.Search;

import com.connect.Comments.models.Comments;
import com.connect.NewsFeed.NewsFeedApi;
import com.connect.NewsFeed.model.Feed;
import com.connect.Search.model.Search;

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

public interface SearchApi {

    String BASE_URL = "http://192.168.42.206:8000/firstapp/";


    @Headers("Content-Type: application/json")
    @POST("search/")
    Observable<List<Search>> getSearch(
            @Body Map<String, String> body,
            @HeaderMap Map<String, String> headers
    );



    public static Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());

    static Retrofit retrofit = retrofitBuilder.build();

    static SearchApi requestApi = retrofit.create(SearchApi.class);

    public static SearchApi getRequestApi(){
        return requestApi;
    }
}

