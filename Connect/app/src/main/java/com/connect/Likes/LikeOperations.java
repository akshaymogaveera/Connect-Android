package com.connect.Likes;

import android.content.SharedPreferences;
import android.util.Log;

import com.connect.Auth.AuthAPI;
import com.connect.Auth.LoginActivity;
import com.connect.Home.HomeActivity;
import com.connect.NewsFeed.NewsFeedFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.ResourceBundle;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class LikeOperations {

    private static final String TAG = "LikeOperations";

    public static void getCallPostLike(String id){



        LikeApi likePost = LikeApi.getRequestApi();
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+ NewsFeedFragment.sharedpreferences.getString("accessToken", null));
        headerMap.put("Content-Type", "application/json");

        HashMap<String, String> body = new HashMap<String, String>();
        body.put("id",id);

        Call<ResponseBody> call = likePost.likePost(body, headerMap);

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

                        Log.d(TAG, "Post Liked " + responseCode);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    Log.d(TAG, "Post not Liked " + responseCode);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: Something went wrong: " + t.getMessage());

            }
        });

    }

}
