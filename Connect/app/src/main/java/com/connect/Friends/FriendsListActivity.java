package com.connect.Friends;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.connect.Comments.CommentsApi;
import com.connect.Friends.model.Friend;
import com.connect.Friends.model.User2;
import com.connect.Search.SearchRecyclerView;
import com.connect.Search.model.SearchLinear;
import com.connect.main.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;


public class FriendsListActivity extends AppCompatActivity {

    private static final String TAG = "FriendsListActivity";

    SharedPreferences sharedpreferences;
    ArrayList<SearchLinear> list;
    HashMap<String, SearchLinear> mapping;
    private RecyclerView mListView;
    SearchRecyclerView adapter;
    private Context mContext;
    String BASE_URL;
    int pageMutualFriends=1;
    int pageFriends=1;
    int page=1;
    HashSet<Integer> pageSetFriends = new HashSet<>();
    HashSet<Integer> pageSetMutualFriends = new HashSet<>();
    LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        BASE_URL = "http://"+ getResources().getString(R.string.ip)+":8000";

        sharedpreferences = getSharedPreferences("myKey", MODE_PRIVATE);

        mContext = FriendsListActivity.this;

        mListView = (RecyclerView) findViewById(R.id.friendsList);

        list = new ArrayList<>();
        mapping = new HashMap<>();

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String action = intent.getStringExtra("action");

        if(action.contains("mutual")){

            executeMutualFriendList(id, 1);
            pageSetMutualFriends.add(1);
        }
        else{
            executeFriendList(id,1);
            pageSetFriends.add(1);
        }

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
                            page = (totalItemCount[0]/14)+1;
                            Log.v("...", "Page "+page);

                            if(action.contains("mutual")){

                                if(!pageSetMutualFriends.contains(page)) {
                                    pageSetMutualFriends.add(page);
                                    executeMutualFriendList(id,page);
                                }
                            }
                            else{
                                if(!pageSetFriends.contains(page)) {
                                    executeFriendList(id, page);
                                    pageSetFriends.add(1);
                                }
                            }



                            loading[0] = true;
                        }
                    }
                }
            }
        });

    }

    private void executeFriendList(String id, int pageFriends){


        getFriendList(id, pageFriends)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Friend, ObservableSource<Friend>>() {
                    @Override
                    public ObservableSource<Friend> apply(Friend friend) throws Exception {
                        return getProfilePicObservable(friend);


                    }
                })
                .debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Friend>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //disposables.add(d);
                    }

                    @Override
                    public void onNext(Friend post) {
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

    private void executeMutualFriendList(String id, int pageMutualFriends){


        getMutualFriendList(id, pageMutualFriends)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<User2, ObservableSource<User2>>() {
                    @Override
                    public ObservableSource<User2> apply(User2 friend) throws Exception {
                        return getMutualProfilePicObservable(friend);


                    }
                })
                .debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User2>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //disposables.add(d);
                    }

                    @Override
                    public void onNext(User2 user2) {
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


    public Observable<Friend> getProfilePicObservable(final Friend friend){

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));
        HashMap<String, String> body = new HashMap<String, String>();

        return CommentsApi.getRequestApi()
                .getProfilePic(new HashMap<String, String>()
                {{
                    put("id",friend.getUser2().getId().toString());

                }},headerMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ResponseBody, Friend>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public Friend apply(ResponseBody profilePic) throws Exception {

                        JSONObject data = new JSONObject(profilePic.string());
                        String profile_pic = data.getString("profile_pic");
                        System.out.println("Profile Pic: "+profile_pic);
                        //post.setComments(comments);

                        SearchLinear temp = mapping.get(friend.getId().toString());
                        int pos = list.indexOf(temp);
                        temp.setProfile_pic(BASE_URL+profile_pic);
                        mapping.replace(friend.getId().toString(),temp);
                        list.set(pos,temp);

                        //adapter.notifyDataSetChanged();
                        adapter.notifyItemChanged(pos);

                        return friend;
                    }
                });

    }

    public Observable<User2> getMutualProfilePicObservable(final User2 friend){

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));
        HashMap<String, String> body = new HashMap<String, String>();

        return CommentsApi.getRequestApi()
                .getProfilePic(new HashMap<String, String>()
                {{
                    put("id",friend.getId().toString());

                }},headerMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ResponseBody, User2>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public User2 apply(ResponseBody profilePic) throws Exception {

                        JSONObject data = new JSONObject(profilePic.string());
                        String profile_pic = data.getString("profile_pic");
                        System.out.println("Profile Pic: "+profile_pic);
                        //post.setComments(comments);

                        SearchLinear temp = mapping.get(friend.getId().toString());
                        int pos = list.indexOf(temp);
                        temp.setProfile_pic(BASE_URL+profile_pic);
                        mapping.replace(friend.getId().toString(),temp);
                        list.set(pos,temp);

                        //adapter.notifyDataSetChanged();
                        adapter.notifyItemChanged(pos);

                        return friend;
                    }
                });

    }



    private Observable<Friend> getFriendList(String id, int pageFriends){

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));

        return FriendsApi.getRequestApi()
                .getFriendList(headerMap,Integer.parseInt(id),pageFriends)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Friend>, ObservableSource<Friend>>() {
                    @Override
                    public ObservableSource<Friend> apply(final List<Friend> friends) throws Exception {

                        for (Friend friend: friends) {
                            //NotificationsLinear temp = new NotificationsLinear(friend.getId().toString(),friend.getUser1().getId().toString(),friend.getUser1().getUsername(),"".toString(), friend.getUser1().getUsername()+" sent you a friend request","","",new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(date));
                            //mapping.put(likeN.getPostID().toString(),temp);
                            SearchLinear temp = new SearchLinear("drawable://" + R.drawable.arizona_dessert,friend.getUser2().getFirstName(), friend.getUser2().getLastName(), friend.getUser2().getId().toString());
                            list.add(temp);
                            mapping.put(friend.getId().toString(),temp);
                        }

                        if(pageFriends == 1){
                            adapter = new SearchRecyclerView(mContext, R.layout.search_linear_view, list , mapping);
                            mListView.setAdapter(adapter);
                            linearLayoutManager = new LinearLayoutManager(mContext);
                            mListView.setLayoutManager(linearLayoutManager);
                        }
                        else{
                            adapter.notifyDataSetChanged();
                        }

                        //adapter.setPosts(posts);
                        System.out.println("Commmnet called +++++++++++++++");
                        return Observable.fromIterable(friends)
                                .subscribeOn(Schedulers.io());
                    }
                });
    }

    private Observable<User2> getMutualFriendList(String id, int pageMutualFriends){

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));

        return FriendsApi.getRequestApi()
                .getMutualFriendList(headerMap, Integer.parseInt(id), pageMutualFriends)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<User2>, ObservableSource<User2>>() {
                    @Override
                    public ObservableSource<User2> apply(final List<User2> friends) throws Exception {

                        for (User2 friend: friends) {
                            //NotificationsLinear temp = new NotificationsLinear(friend.getId().toString(),friend.getUser1().getId().toString(),friend.getUser1().getUsername(),"".toString(), friend.getUser1().getUsername()+" sent you a friend request","","",new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(date));
                            //mapping.put(likeN.getPostID().toString(),temp);
                            SearchLinear temp = new SearchLinear("drawable://" + R.drawable.arizona_dessert,friend.getFirstName(), friend.getLastName(), friend.getId().toString());
                            list.add(temp);
                            mapping.put(friend.getId().toString(),temp);
                        }

                        if(pageMutualFriends == 1){

                            adapter = new SearchRecyclerView(mContext, R.layout.search_linear_view, list , mapping);
                            mListView.setAdapter(adapter);
                            linearLayoutManager = new LinearLayoutManager(mContext);
                            mListView.setLayoutManager(linearLayoutManager);
                        }
                        else{
                            adapter.notifyDataSetChanged();
                        }


                        //adapter.setPosts(posts);
                        System.out.println("Commmnet called +++++++++++++++");
                        return Observable.fromIterable(friends)
                                .subscribeOn(Schedulers.io());
                    }
                });
    }


}
