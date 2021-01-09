package com.connect.Auth;

import android.content.Context;
import android.util.Log;

import com.connect.main.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class Validate {


    private static final String TAG = "Validate";
    public static Cache cache;
    public static HttpLoggingInterceptor httpLoggingInterceptor;


    public static HashMap<String,String> login(Context mContext,String Username, String Password) throws IOException {

        final HashMap<String,String> userDetails = new HashMap<>();



        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(Username, Password))
                .build();


        httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        File httpCacheDirectory = new File(mContext.getCacheDir(), "offlineCache");

        //10 MB
        cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(new BasicAuthInterceptor(Username, Password))
                .addInterceptor(httpLoggingInterceptor)
                .addNetworkInterceptor(provideCacheInterceptor())
                .addInterceptor(provideOfflineCacheInterceptor())
                .build();



        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://"+mContext.getResources().getString(R.string.ip)+":8000/firstapp/basic/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AuthAPI redditAPI = retrofit.create(AuthAPI.class);
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Content-Type", "application/json");


        Call<ResponseBody> call = redditAPI.login(headerMap);

        //ResponseBody response = call.execute().body();

        //Log.d(TAG,call.execute().body().toString());


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: Server Response: " + response.toString());

                String responseCode = String.valueOf(response.code());
                Log.d(TAG, "onResponse: json: " + responseCode);
                //JSONObject data = null;
                // data = new JSONObject(json);
//                         //   Log.d(TAG, "onResponse: data: " + data.optString("json"));
                if (responseCode.contentEquals("200")) {

                    try {
                        JSONObject data = new JSONObject(response.body().string());
                        String accessToken = data.getString("access");
                        String refreshToken = data.getString("refresh");
                        String ID = data.getString("id");
                        Log.d(TAG,accessToken);
                        userDetails.put("accessToken",accessToken);
                        userDetails.put("refreshToken",refreshToken);
                        userDetails.put("ID",ID);
                        LoginActivity.checkData(mContext,userDetails,responseCode);

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }


                } else {
                    LoginActivity.checkData(mContext,userDetails,responseCode);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: Something went wrong: " + t.getMessage());

            }
        });


        return userDetails;
    }


    public static Interceptor provideCacheInterceptor() {

        return new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request request = chain.request();
                okhttp3.Response originalResponse = chain.proceed(request);
                String cacheControl = originalResponse.header("Cache-Control");

                if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                        cacheControl.contains("must-revalidate") || cacheControl.contains("max-stale=0")) {


                    CacheControl cc = new CacheControl.Builder()
                            .maxStale(1, TimeUnit.DAYS)
                            .build();



                    request = request.newBuilder()
                            .cacheControl(cc)
                            .build();

                    return chain.proceed(request);

                } else {
                    return originalResponse;
                }
            }
        };

    }


    public static Interceptor provideOfflineCacheInterceptor() {

        return new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                try {
                    return chain.proceed(chain.request());
                } catch (Exception e) {


                    CacheControl cacheControl = new CacheControl.Builder()
                            .onlyIfCached()
                            .maxStale(1, TimeUnit.DAYS)
                            .build();

                    Request offlineRequest = chain.request().newBuilder()
                            .cacheControl(cacheControl)
                            .build();
                    return chain.proceed(offlineRequest);
                }
            }
        };
    }
}
