package com.connect.Post;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.connect.Likes.LikeApi;
import com.connect.NewsFeed.NewsFeedFragment;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostApiCall {

    private static final String TAG = "CreatePostApiCall";

    public static void createPost(Context mContext,String text, String imgUrl, ByteArrayOutputStream out){

        PostApi createPost = PostApi.getRequestApi();
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+ NewsFeedFragment.sharedpreferences.getString("accessToken", null));
        RequestBody textBody = RequestBody.create(MediaType.parse("text/plain"),text);
        //out = compressImage(imgUrl);

        Map<String,RequestBody> bodyMap = new HashMap<>();

        bodyMap.put("text",textBody);

        RequestBody profile_pic = RequestBody.create(MediaType.parse("application/octet-stream"), out.toByteArray());
        String[] bits = imgUrl.split("/");
        String lastOne = bits[bits.length-1];


        Call<ResponseBody> call = createPost.createPostWithImage(MultipartBody.Part.createFormData("post_pics",lastOne,profile_pic),bodyMap, headerMap);

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

                        Log.d(TAG, "Post Created " + responseCode);
                        Toast.makeText(mContext, "Post Uploaded", Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        Toast.makeText(mContext, "Post Upload Failed !", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }


                } else {
                    Log.d(TAG, "Post not Created " + responseCode);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: Something went wrong: " + t.getMessage());

            }
        });

    }

    public static void deletePost(Context mContext,String id){


        PostApi deletePost = PostApi.getRequestApi();
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+ NewsFeedFragment.sharedpreferences.getString("accessToken", null));
        headerMap.put("Content-Type", "application/json");
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("id",id);

        Call<ResponseBody> call = deletePost.deletePost(body, headerMap);

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

                        Log.d(TAG, "Post Created " + responseCode);
                        Toast.makeText(mContext, "Post Deleted", Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        Toast.makeText(mContext, "Post Deletion Failed !", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }


                } else {
                    Log.d(TAG, "Post Deletion Failed " + responseCode);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: Something went wrong: " + t.getMessage());

            }
        });

    }
}
