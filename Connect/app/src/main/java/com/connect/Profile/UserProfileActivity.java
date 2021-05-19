package com.connect.Profile;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.connect.Comments.CommentsApi;
import com.connect.Friends.FriendsApi;
import com.connect.Friends.FriendsListActivity;
import com.connect.NewsFeed.Card;
import com.connect.NewsFeed.NewsFeedApi;
import com.connect.NewsFeed.NewsFeedFragment;
import com.connect.NewsFeed.NewsFeedRecyclerView;
import com.connect.NewsFeed.model.Feed;
import com.connect.Post.PostApi;
import com.connect.UserProfileEdit.UserProfileAPI;
import com.connect.UserProfileEdit.models.UserProfile;
import com.connect.main.R;
import com.connect.main.UniversalImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.connect.Friends.Operations.addFriendCall;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    AlertDialog.Builder builder;
    static Button friendStatus;
    TextView countPosts, countFriends, mDisplayName, mUsername,location, mutuals;
    String BASE_URL;
    private CircleImageView mProfilePhoto;
    SharedPreferences sharedpreferences;
    ArrayList<Card> list;
    HashMap<String, Card> mapping;
    private RecyclerView mListView;
    boolean liked;
    //CustomListAdapter adapter;
    //ProfileFeedRecyclerView adapter;
    NewsFeedRecyclerView adapter;
    String countLikes, countComments, id;
    private Context mContext = UserProfileActivity.this;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    int page=1;
    HashSet<Integer> pageSet = new HashSet<>();
    LinearLayoutManager linearLayoutManager;
    TextView emptyView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        //BASE_URL = "http://"+ getResources().getString(R.string.ip)+":8000";
        BASE_URL = "https://"+ getResources().getString(R.string.ip);
        emptyView = (TextView) findViewById(R.id.empty_view);
        sharedpreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        countPosts = findViewById(R.id.countPosts);
        countFriends = findViewById(R.id.countFollowers);
        mProfilePhoto = findViewById(R.id.profile_image);
        mDisplayName = findViewById(R.id.display_name);
        mUsername =  findViewById(R.id.profileName);
        friendStatus =  findViewById(R.id.friendStatus);
        location =  findViewById(R.id.location);
        mutuals =  findViewById(R.id.mutuals);

        mListView = (RecyclerView) findViewById(R.id.listView);
        mSwipeRefreshLayout = findViewById(R.id.swiperefresh_newsfeed);
        countLikes = "0";
        countComments = "0";
        liked = false;
        list = new ArrayList<>();
        mapping = new HashMap<>();

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        getFriendStatus();
        getPostsCount();
        getFriendsCount();
        getProfileData();
        getMutualFriendsCount();

        friendStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Friend status button clicked");
                if (friendStatus.getText().toString().contains("Friends")){
                    dialogboxToDeleteFriends("Do you want to remove friend ?");
                }
                else if (friendStatus.getText().toString().contains("Sent")){
                    dialogboxToDeleteFriends("Do you want to cancel request ?");
                }
                else{
                    addFriendCall(mContext, id, true);
                }

            }
        });

        countFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to EditProfileActivity");
                Intent intent1 = new Intent(UserProfileActivity.this, FriendsListActivity.class);
                intent1.putExtra("id", id);
                intent1.putExtra("action", "friendList");
                startActivity(intent1);
                //finish();
            }
        });

        mutuals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to EditProfileActivity");
                Intent intent1 = new Intent(UserProfileActivity.this, FriendsListActivity.class);
                intent1.putExtra("id", id);
                intent1.putExtra("action", "mutualfriendsList");
                startActivity(intent1);
                //finish();
            }
        });

        final boolean[] loading = {true};
        final int[] pastVisiblesItems = new int[1];
        final int[] visibleItemCount = new int[1];
        final int[] totalItemCount = new int[1];

        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount[0] = linearLayoutManager.getChildCount();
                    totalItemCount[0] = linearLayoutManager.getItemCount();
                    pastVisiblesItems[0] = linearLayoutManager.findFirstVisibleItemPosition();

                    if (loading[0]) {
                        if ((visibleItemCount[0] + pastVisiblesItems[0]) >= totalItemCount[0]) {
                            loading[0] = false;
                            Log.v("...", "Last Item Wow !");
                            Log.v("...", "Visible "+visibleItemCount[0]);
                            Log.v("...", "pastVisiblesItems "+pastVisiblesItems[0]);
                            Log.v("...", "totalItemCount "+totalItemCount[0]);
                            // Do pagination.. i.e. fetch new data
                            page = (totalItemCount[0]/2)+1;
                            Log.v("...", "Page "+page);

                            if(!pageSet.contains(page)){
                                pageSet.add(page);
                                executeObservables(page);
                            }

                            loading[0] = true;
                        }
                    }
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to make your refresh action
                // CallYourRefreshingMethod();
                list.clear();
                mapping.clear();
                getFriendStatus();
                getPostsCount();
                getFriendsCount();
                getProfileData();
                pageSet.clear();
                executeObservables(1);
                pageSet.add(1);
                getMutualFriendsCount();
                mSwipeRefreshLayout.setRefreshing(false);
                //adapter.notifyDataSetChanged();
            }



        });

        executeObservables(1);
        pageSet.add(1);
    }

    private void dialogboxToDeleteFriends(String msg) {
        String ID =id;
        builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addFriendCall(mContext, ID, false);
                        //Toast.makeText(getApplicationContext(),"you choose yes action for alertbox",
                                //oast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();

                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Alert");
        alert.show();
    }

    private void getPostsCount(){

        PostApi postApi = PostApi.getRequestApi();
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+ NewsFeedFragment.sharedpreferences.getString("accessToken", null));


        HashMap<String, String> body = new HashMap<String, String>();
        body.put("id",id);

        Call<ResponseBody> call = postApi.getUsersPostCount(body, headerMap);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: Server Response: " + response.toString());

                String responseCode = String.valueOf(response.code());
                Log.d(TAG, "onResponse: json: " + responseCode);
                if (responseCode.contentEquals("200")) {

                    try {

                        Log.d(TAG, "Post Count fetched " + responseCode);
                        JSONObject data = new JSONObject(response.body().string());
                        String count = data.getString("count");
                        countPosts.setText(count);

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

    private void getFriendsCount(){

        FriendsApi friendsApi = FriendsApi.getRequestApi();
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+ NewsFeedFragment.sharedpreferences.getString("accessToken", null));

        HashMap<String, String> body = new HashMap<String, String>();
        body.put("id",id);

        Call<ResponseBody> call = friendsApi.getUsersFriendsCount(body, headerMap);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: Server Response: " + response.toString());

                String responseCode = String.valueOf(response.code());
                Log.d(TAG, "onResponse: json: " + responseCode);
                if (responseCode.contentEquals("200")) {

                    try {

                        Log.d(TAG, "Friends Count fetched " + responseCode);
                        JSONObject data = new JSONObject(response.body().string());
                        String count = data.getString("count");
                        countFriends.setText(count);

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

    public void getProfileData(){

        Retrofit userProfile = new Retrofit.Builder()
                .baseUrl(BASE_URL+"/firstapp/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserProfileAPI userProfileAPI = userProfile.create(UserProfileAPI.class);
        HashMap<String, String> headerMap = new HashMap<String, String>();
        HashMap<String, String> body = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));
        body.put("id",id);
        Call<UserProfile> call = userProfileAPI.getData(body,headerMap);

        call.enqueue(new Callback<UserProfile>() {


            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                Log.d(TAG, "onResponse: Server Response: " + response.toString());
                Log.d(TAG, "onResponse: received information: " + response.errorBody());

                mUsername.setText(response.body().getInfo().getUser().getUsername());
                mUsername.setEnabled(false);
                location.setText(response.body().getInfo().getCity());
                mDisplayName.setText(response.body().getInfo().getUser().getFirstName()+" "+response.body().getInfo().getUser().getLastName());
                mDisplayName.setEnabled(false);
                UniversalImageLoader.setImage(BASE_URL+response.body().getInfo().getProfilePic(), mProfilePhoto, null, "");
                System.out.println(response.body().getSelf());
                System.out.println(response.body().getInfo().getUser().getFirstName());
                //data.put(f.getAuthor().getUsername(),"http://192.168.42.179:8000"+f.getPost_pics());
                //list.add(new Card("http://192.168.42.206:8000"+f.getPost_pics(),f.getAuthor().getUsername()));

            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Log.e(TAG, "onFailure: Something went wrong: " + t.getMessage() );
                //Toast.makeText(Tab2Fragment.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void getMutualFriendsCount(){

        FriendsApi friendsApi = FriendsApi.getRequestApi();
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+ NewsFeedFragment.sharedpreferences.getString("accessToken", null));

        HashMap<String, String> body = new HashMap<String, String>();
        body.put("id",id);

        Call<ResponseBody> call = friendsApi.getMutualFriends(body, headerMap);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: Server Response: " + response.toString());

                String responseCode = String.valueOf(response.code());
                Log.d(TAG, "onResponse: json: " + responseCode);
                if (responseCode.contentEquals("200")) {

                    try {

                        Log.d(TAG, "Mutual Friends Count fetched " + responseCode);
                        JSONObject data = new JSONObject(response.body().string());
                        String count = data.getString("count");
                        mutuals.setText(count+" Mutual Friends");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    Log.d(TAG, "Mutual Friends Count Not fetched " + responseCode);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: Something went wrong: " + t.getMessage());

            }
        });


    }

    private void executeObservables(int page){

        getPostsObservable(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Feed, ObservableSource<Feed>>() {
                    @Override
                    public ObservableSource<Feed> apply(Feed post) throws Exception {
                        return getLikesObservable(post);

                    }
                })
                .flatMap(new Function<Feed, ObservableSource<Feed>>() {
                    @Override
                    public ObservableSource<Feed> apply(Feed post) throws Exception {
                        return getCommentsObservable(post);

                    }
                })
                .flatMap(new Function<Feed, ObservableSource<Feed>>() {
                    @Override
                    public ObservableSource<Feed> apply(Feed post) throws Exception {
                        return getLikedObservable(post);

                    }
                })
                .flatMap(new Function<Feed, ObservableSource<Feed>>() {
                    @Override
                    public ObservableSource<Feed> apply(Feed post) throws Exception {
                        return getProfilePicObservable(post);

                    }
                })
                .debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Feed>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //disposables.add(d);
                    }

                    @Override
                    public void onNext(Feed post) {
                        //updatePost(post);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }

    private Observable<Feed> getPostsObservable(int page){

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));

        return NewsFeedApi.getRequestApi()
                .getUserFeed(headerMap, Integer.parseInt(id),page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Feed>, ObservableSource<Feed>>() {
                    @Override
                    public ObservableSource<Feed> apply(final List<Feed> posts) throws Exception {

                        for (Feed f: posts) {
                            System.out.println(f.getAuthor().getUsername());
                            //data.put(f.getAuthor().getUsername(),"http://192.168.42.179:8000"+f.getPost_pics());
                            Card temp = new Card(f.getAuthor().getId(), f.getId(), BASE_URL+f.getPost_pics(),f.getAuthor().getUsername(), countLikes, countComments, liked, f.getText(), "drawable://" + R.drawable.connect, f.getCreated_date());
                            mapping.put(f.getId(),temp);
                            list.add(temp);
                        }

                        if (page == 1) {
                            //adapter = new CustomListAdapter(NewsFeedActivity.this, R.layout.card_layout_main, list);
                            adapter = new NewsFeedRecyclerView(mContext, R.layout.card_layout_main, list, mapping);
                            mListView.setAdapter(adapter);
                            linearLayoutManager = new LinearLayoutManager(mContext);
                            mListView.setLayoutManager(linearLayoutManager);
                        }else {
                            adapter.notifyDataSetChanged();
                        }

                        if(list.size() == 0){
                            mListView.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        //adapter.setPosts(posts);
                        System.out.println(posts.get(0).getAuthor()+"---------");
                        return Observable.fromIterable(posts)
                                .subscribeOn(Schedulers.io());
                    }
                });
    }


    public Observable<Feed> getLikesObservable(final Feed post){

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));
        HashMap<String, String> body = new HashMap<String, String>();

        return NewsFeedApi.getRequestApi()
                .countLikesObs(new HashMap<String, String>()
                {{
                    put("id", post.getId());

                }},headerMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ResponseBody, Feed>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public Feed apply(ResponseBody comments) throws Exception {

                        JSONObject data = new JSONObject(comments.string());
                        String count = data.getString("count");
                        System.out.println(count+" Likes -------------"+post.getId());
                        //post.setComments(comments);

                        Card temp = mapping.get(post.getId());
                        int pos = list.indexOf(temp);
                        temp.setCountLikes(count);
                        mapping.replace(post.getId(),temp);
                        list.set(pos,temp);

                        //adapter.notifyDataSetChanged();
                        //adapter.notifyItemChanged(pos);

                        return post;
                    }
                });

    }

    private Observable<Feed> getCommentsObservable(final Feed post){

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));
        HashMap<String, String> body = new HashMap<String, String>();

        return NewsFeedApi.getRequestApi()
                .countCommentsObs(new HashMap<String, String>()
                {{
                    put("id", post.getId());

                }},headerMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ResponseBody, Feed>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public Feed apply(ResponseBody comments) throws Exception {

                        JSONObject data = new JSONObject(comments.string());
                        String count = data.getString("count");
                        System.out.println(count+" Comment -------------"+post.getId());
                        //post.setComments(comments);

                        Card temp = mapping.get(post.getId());
                        int pos = list.indexOf(temp);
                        temp.setCountComments(count);
                        mapping.replace(post.getId(),temp);
                        list.set(pos,temp);

                        //adapter.notifyDataSetChanged();
                        //adapter.notifyItemChanged(pos);

                        return post;
                    }
                });

    }

    private Observable<Feed> getLikedObservable(final Feed post){

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));
        HashMap<String, String> body = new HashMap<String, String>();

        return NewsFeedApi.getRequestApi()
                .getLiked(new HashMap<String, String>()
                {{
                    put("id", post.getId());

                }},headerMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ResponseBody, Feed>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public Feed apply(ResponseBody comments) throws Exception {

                        boolean liked = false;
                        JSONObject data = new JSONObject(comments.string());
                        String responsedata = data.getString("liked");

                        if(responsedata.equals("True"))
                            liked = true;
                        System.out.println(liked+" Liked -------------"+post.getId());
                        //post.setComments(comments);

                        Card temp = mapping.get(post.getId());
                        int pos = list.indexOf(temp);
                        temp.setLiked(liked);
                        mapping.replace(post.getId(),temp);
                        list.set(pos,temp);

                        //adapter.notifyDataSetChanged();
                        //adapter.notifyItemChanged(pos);

                        return post;
                    }
                });

    }

    public Observable<Feed> getProfilePicObservable(final Feed post){

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));
        HashMap<String, String> body = new HashMap<String, String>();

        return CommentsApi.getRequestApi()
                .getProfilePic(new HashMap<String, String>()
                {{
                    put("id",post.getAuthor().getId());

                }},headerMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ResponseBody, Feed>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public Feed apply(ResponseBody profilePic) throws Exception {

                        JSONObject data = new JSONObject(profilePic.string());
                        String profile_pic = data.getString("profile_pic");
                        System.out.println("Profile Pic: "+profile_pic);
                        //post.setComments(comments);

                        Card temp = mapping.get(post.getId());
                        int pos = list.indexOf(temp);
                        temp.setProfileImgUrl(BASE_URL+profile_pic);
                        mapping.replace(post.getId(),temp);
                        list.set(pos,temp);

                        //adapter.notifyDataSetChanged();
                        adapter.notifyItemChanged(pos);

                        return post;
                    }
                });

    }

    private void getFriendStatus(){

        FriendsApi friendsApi = FriendsApi.getRequestApi();
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+ NewsFeedFragment.sharedpreferences.getString("accessToken", null));

        HashMap<String, String> body = new HashMap<String, String>();
        body.put("id",id);

        Call<ResponseBody> call = friendsApi.getFriendStatus(body, headerMap);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: Server Response: " + response.toString());

                String responseCode = String.valueOf(response.code());
                Log.d(TAG, "onResponse: json: " + responseCode);
                if (responseCode.contentEquals("200")) {

                    try {

                        Log.d(TAG, "Friends Status fetched " + responseCode);
                        JSONObject data = new JSONObject(response.body().string());
                        String count = data.getString("status");
                        if ("2".equals(count)){
                            friendStatus.setText("Friends");
                        }
                        else if ("1".equals(count)){
                            friendStatus.setText("Request Sent");

                        }
                        else if ("3".equals(count)){
                            friendStatus.setText("Accept Friend Request");
                        }
                        else{
                            friendStatus.setText("Add Friend");
                        }
                        //friendStatus.setText(count);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    Log.d(TAG, "Friends Status fetch Failed " + responseCode);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: Something went wrong: " + t.getMessage());

            }
        });


    }

    public static void setDisplay(String text){

        friendStatus.setText(text);

    }
}