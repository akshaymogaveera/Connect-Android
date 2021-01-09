package com.connect.Search;

import com.connect.Home.HomeActivity;
import com.connect.Search.model.Search;
import com.connect.main.R;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SearchApi {

    String BASE_URL="http://"+ HomeActivity.getContext().getResources().getString(R.string.ip)+":8000/firstapp/";


//    @Headers("Content-Type: application/json")
//    @POST("search/")
//    Observable<List<Search>> getSearch(
//            @Body Map<String, String> body,
//            @HeaderMap Map<String, String> headers
//    );

    @Headers("Content-Type: application/json")
    @GET("search/{input}/")
    Observable<List<Search>> getSearch(
            @HeaderMap Map<String, String> headers,
            @Path("input") String input,
            @Query("page") int page
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

