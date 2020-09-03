package com.connect.main;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.connect.Auth.AuthAPI;
import com.connect.Auth.BasicAuthInterceptor;
import com.connect.Auth.LoginActivity;
import com.connect.Home.HomeActivity;

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

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    SharedPreferences sharedpreferences;
    ProgressBar progMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: started.");
        progMain = findViewById(R.id.MainProgressBar);

        sharedpreferences = getSharedPreferences("myKey",MODE_PRIVATE);

//        SharedPreferences.Editor editor = sharedpreferences.edit();
//        editor.remove("username");
//        editor.putString(getString(R.string.username), "akshay");
//        editor.commit();


        String unameSharedpref = sharedpreferences.getString("id", null);

        //Log.d(TAG,unameSharedpref);

        if (unameSharedpref == null){

            Log.d(TAG,"User Not Present");
            Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent1);
            finish();

        }
        else{

            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            File httpCacheDirectory = new File(getCacheDir(), "offlineCache");

            //10 MB
            Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .addInterceptor(httpLoggingInterceptor)
                    .addNetworkInterceptor(provideCacheInterceptor())
                    .addInterceptor(provideOfflineCacheInterceptor())
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    //.client(httpClient)
                    .baseUrl("http://192.168.42.206:8000/firstapp/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            AuthAPI authAPI = retrofit.create(AuthAPI.class);
            HashMap<String, String> headerMap = new HashMap<String, String>();
            headerMap.put("Content-Type", "application/json");
            headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));
            Call<ResponseBody> call = authAPI.validateToken(headerMap);

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
                            Log.d(TAG,accessToken);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("accessToken",accessToken);
                            editor.putString("refreshToken", data.getString("refresh"));
                            editor.putString("id", data.getString("id"));
                            editor.commit();
                            // call home
                            Log.d(TAG,"User Present");
                            Intent intent1 = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent1);

                            finish();
                        } catch (JSONException | IOException e) {

                            e.printStackTrace();
                        }


                    } else {
                        Log.d(TAG,"User Not Present");
                        Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent1);
                        finish();
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "onFailure: Something went wrong: " + t.getMessage());

                }
            });



        }

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
                            .removeHeader("Vary")
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
                            .removeHeader("Vary")
                            .cacheControl(cacheControl)
                            .build();
                    return chain.proceed(offlineRequest);
                }
            }
        };
    }
}