package com.connect.Auth;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class Validate {


    private static final String TAG = "Validate";

    public static HashMap<String,String> login(Context mContext,String Username, String Password) throws IOException {

        final HashMap<String,String> userDetails = new HashMap<>();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(Username, Password))
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://192.168.42.206:8000/firstapp/basic/")
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
}
