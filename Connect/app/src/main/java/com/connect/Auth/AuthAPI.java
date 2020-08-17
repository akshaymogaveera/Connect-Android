package com.connect.Auth;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;



/**
 * Created by User on 5/1/2017.
 */

public interface AuthAPI {


    @POST("auth/")
    Call<ResponseBody> login(
            @HeaderMap Map<String, String> headers
    );

    @GET("validate/token/")
    Call <ResponseBody> validateToken(
            @HeaderMap Map<String , String > headers
    );
}