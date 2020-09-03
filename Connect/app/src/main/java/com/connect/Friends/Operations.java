package com.connect.Friends;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.connect.NewsFeed.NewsFeedFragment;
import org.json.JSONObject;
import java.util.HashMap;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.connect.Profile.UserProfileActivity.setDisplay;

public class Operations {

    private static final String TAG = "Operations";

    public static void addFriendCall(Context mContext, String id, boolean action){

        FriendsApi friendsApi = FriendsApi.getRequestApi();
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+ NewsFeedFragment.sharedpreferences.getString("accessToken", null));
        headerMap.put("Content-Type", "application/json");

        HashMap<String, String> body = new HashMap<String, String>();
        if (action)
            body.put("action","add");
        else
            body.put("action","delete");
        body.put("id",id);

        Call<ResponseBody> call = friendsApi.addFriend(body, headerMap);
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

                        Log.d(TAG, "Add Friend sent " + responseCode);
                        //Toast.makeText(mContext, "Post Uploaded", Toast.LENGTH_LONG).show();
                        JSONObject data = new JSONObject(response.body().string());
                        String status = data.getString("status");
                        Log.d(TAG, " Friend status  " + status);

                        if("100".equals(status)){
                            setDisplay("Request Sent");
                        }
                        else if("102".equals(status)){
                            setDisplay("Friends");
                        }
                        else if("105".equals(status)){
                            Toast.makeText(mContext, "Request Already Sent !", Toast.LENGTH_LONG).show();
                        }
                        else if("101".equals(status)){
                            Toast.makeText(mContext, "Already Friends", Toast.LENGTH_LONG).show();
                        }
                        else if("301".equals(status)){
                            setDisplay("Add Friend");
                            Toast.makeText(mContext, "Friend Removed", Toast.LENGTH_LONG).show();
                        }
                        else if("303".equals(status)){
                            setDisplay("Add Friend");
                            Toast.makeText(mContext, "Request Cancelled", Toast.LENGTH_LONG).show();
                        }


                    } catch (Exception e) {
                        Toast.makeText(mContext, "Please try after sometime", Toast.LENGTH_LONG).show();
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
}
