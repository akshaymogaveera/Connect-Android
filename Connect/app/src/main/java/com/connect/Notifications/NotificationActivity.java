package com.connect.Notifications;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.connect.Comments.CommentsApi;
import com.connect.Friends.FriendsApi;
import com.connect.Friends.model.Friend;
import com.connect.Notifications.models.Notification;
import com.connect.Notifications.models.NotificationsLinear;
import com.connect.main.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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

public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = "NotificationActivity";

    SharedPreferences sharedpreferences;
    ArrayList<NotificationsLinear> list;
    HashMap<String, NotificationsLinear> mapping;
    private RecyclerView mListView;
    NotificationRecyclerView adapter;
    private Context mContext;
    EditText searchText;
    Button searchButton;
    String BASE_URL, CommentMsg, LikeMsg;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    HashSet<Integer> pageSetLike = new HashSet<>();
    HashSet<Integer> pageSetComment = new HashSet<>();
    HashSet<Integer> pageSetFrndReq = new HashSet<>();
    LinearLayoutManager linearLayoutManager;
    int pageLike, pageComment, pageFrndReq=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        BASE_URL = "http://"+ getResources().getString(R.string.ip)+":8000";

        sharedpreferences = getSharedPreferences("myKey", MODE_PRIVATE);

        mContext = NotificationActivity.this;

        mListView = (RecyclerView) findViewById(R.id.notificationsRecycleList);

        searchText = (EditText) findViewById(R.id.searchText);

        searchButton = (Button) findViewById(R.id.searchButtonTop);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefresh_notification);

        list = new ArrayList<>();
        mapping = new HashMap<>();

        executeObservablesLikes(1);
        executeObservablesComments(1);
        executeObservablesFrndReq(1);
        pageSetLike.add(1);
        pageSetComment.add(1);
        pageSetFrndReq.add(1);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to make your refresh action
                // CallYourRefreshingMethod();
                list.clear();
                mapping.clear();
                pageSetLike.clear();
                pageSetComment.clear();
                pageSetFrndReq.clear();
                executeObservablesLikes(1);
                executeObservablesComments(1);
                executeObservablesFrndReq(1);
                pageSetLike.add(1);
                pageSetComment.add(1);
                pageSetFrndReq.add(1);
                mSwipeRefreshLayout.setRefreshing(false);
                //adapter.notifyDataSetChanged();
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
                            pageLike = (totalItemCount[0]/9)+1;
                            pageComment = (totalItemCount[0]/9)+1;
                            pageFrndReq = (totalItemCount[0]/9)+1;
                            Log.v("...", "Page "+pageLike +" | "+pageComment+" | "+pageFrndReq);

                            if(!pageSetLike.contains(pageLike)){
                                pageSetLike.add(pageLike);
                                executeObservablesLikes(pageLike);
                            }

                            if(!pageSetComment.contains(pageComment)){
                                pageSetComment.add(pageComment);
                                executeObservablesComments(pageComment);
                            }

                            if(!pageSetFrndReq.contains(pageFrndReq)){
                                pageSetFrndReq.add(pageFrndReq);
                                executeObservablesFrndReq(pageFrndReq);
                            }

                            loading[0] = true;
                        }
                    }
                }
            }
        });

    }

    private void executeObservablesLikes(int likePage){

        getLikesNotifications(likePage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Notification, ObservableSource<Notification>>() {
                    @Override
                    public ObservableSource<Notification> apply(Notification notification) throws Exception {
                        return getProfilePicObservable(notification);


                    }
                })
                .debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Notification>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //disposables.add(d);
                    }

                    @Override
                    public void onNext(Notification post) {
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

    private void executeObservablesComments(int commentPage){

        getCommentsNotifications(commentPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Notification, ObservableSource<Notification>>() {
                    @Override
                    public ObservableSource<Notification> apply(Notification notification) throws Exception {
                        return getProfilePicObservable(notification);


                    }
                })
                .debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Notification>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //disposables.add(d);
                    }

                    @Override
                    public void onNext(Notification post) {
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

    private void executeObservablesFrndReq(int frndReqPage){

        getFriendRequestList(frndReqPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Friend, ObservableSource<Friend>>() {
                    @Override
                    public ObservableSource<Friend> apply(Friend friend) throws Exception {
                        return getFriendProfilePicObservable(friend);


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

    private Observable<Notification> getLikesNotifications(int likePage){

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));

        return NotificationApi.getRequestApi()
                .getLikesNotifications(headerMap, likePage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Notification>, ObservableSource<Notification>>() {
                    @Override
                    public ObservableSource<Notification> apply(final List<Notification> likesN) throws Exception {

                        for (Notification likeN: likesN) {

                            if(likeN.getCount().equals(0)){
                                LikeMsg = likeN.getPersonUsername()+" liked your pic";
                            }
                            else{

                                LikeMsg = likeN.getPersonUsername()+" and "+likeN.getCount().toString()+" others liked your pic";
                            }

                            NotificationsLinear temp = new NotificationsLinear(likeN.getPostID().toString(),likeN.getPersonID().toString(),likeN.getPersonUsername(),likeN.getCount().toString(),LikeMsg,BASE_URL+"/media/"+likeN.getPostImgUrl(),BASE_URL+"/media/"+likeN.getPostImgUrl(),new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(likeN.getDate()));
                           // mapping.put(likeN.getPostID().toString(),temp);
                            list.add(temp);
                        }

                        Collections.sort(list, Collections.reverseOrder());
                        mapping.clear();
                        for (NotificationsLinear n: list) {

                            //System.out.println(n.getDate().toString()+"||||||||"+n.getUsername());
                            mapping.put(n.getPostId(),n);

                        }
                        if(likePage == 1){
                            //adapter = new CustomListAdapter(NewsFeedActivity.this, R.layout.card_layout_main, list);
                            adapter = new NotificationRecyclerView(mContext, R.layout.notification_linear_view, list, mapping);
                            mListView.setAdapter(adapter);
                            linearLayoutManager = new LinearLayoutManager(mContext);
                            mListView.setLayoutManager(linearLayoutManager);

                        }
                        else{
                            adapter.notifyDataSetChanged();
                        }

                        //adapter.setPosts(posts);
                        //System.out.println(posts.get(0).getAuthor()+"---------");
                        return Observable.fromIterable(likesN)
                                .subscribeOn(Schedulers.io());
                    }
                });
    }

    private Observable<Notification> getCommentsNotifications(int commentPage){

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));

        return NotificationApi.getRequestApi()
                .getCommentsNotifications(headerMap,commentPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Notification>, ObservableSource<Notification>>() {
                    @Override
                    public ObservableSource<Notification> apply(final List<Notification> likesN) throws Exception {

                        for (Notification likeN: likesN) {

                            if(likeN.getCount().equals(0)){
                                CommentMsg = likeN.getPersonUsername()+" commented on your pic";
                            }
                            else{

                                CommentMsg = likeN.getPersonUsername()+" and "+likeN.getCount().toString()+" others commented on your pic";
                            }


                            String[] datelist = likeN.getDate().split("\\.");
                            String date ="date";
                            if(datelist.length > 1)
                                //System.out.println(datelist.length+"Count -----------"+datelist[0]);
                                date = datelist[0]+"Z";
                            else
                                date = likeN.getDate();
                            //String date = datelist[0]+datelist[1];
                            NotificationsLinear temp = new NotificationsLinear(likeN.getPostID().toString(),likeN.getPersonID().toString(),likeN.getPersonUsername(),likeN.getCount().toString(), CommentMsg,BASE_URL+"/media/"+likeN.getPostImgUrl(),BASE_URL+"/media/"+likeN.getPostImgUrl(),new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(date));
                            //mapping.put(likeN.getPostID().toString(),temp);
                            list.add(temp);
                        }

                        //adapter = new CustomListAdapter(NewsFeedActivity.this, R.layout.card_layout_main, list);
                        Collections.sort(list, Collections.reverseOrder());
                        mapping.clear();
                        for (NotificationsLinear n: list) {

                            //System.out.println(n.getDate().toString()+"||||||||"+n.getUsername());
                            mapping.put(n.getPostId(), n);

                        }
                        adapter.notifyDataSetChanged();
                        //adapter = new NotificationRecyclerView(mContext, R.layout.notification_linear_view, list, mapping);
                        //mListView.setAdapter(adapter);
                        //mListView.setLayoutManager(new LinearLayoutManager(mContext));

                        //adapter.setPosts(posts);
                        System.out.println("Commmnet called +++++++++++++++");
                        return Observable.fromIterable(likesN)
                                .subscribeOn(Schedulers.io());
                    }
                });


    }

    public Observable<Notification> getProfilePicObservable(final Notification NotificationLike){

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));
        HashMap<String, String> body = new HashMap<String, String>();

        return CommentsApi.getRequestApi()
                .getProfilePic(new HashMap<String, String>()
                {{
                    put("id",NotificationLike.getPersonID().toString());

                }},headerMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ResponseBody, Notification>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public Notification apply(ResponseBody profilePic) throws Exception {

                        JSONObject data = new JSONObject(profilePic.string());
                        String profile_pic = data.getString("profile_pic");
                        System.out.println("Profile Pic: "+profile_pic);
                        //post.setComments(comments);

                        NotificationsLinear temp = mapping.get(NotificationLike.getPostID().toString());
                        int pos = list.indexOf(temp);
                        temp.setProfilePicUrl(BASE_URL+profile_pic);
                        mapping.replace(NotificationLike.getPostID().toString(),temp);
                        list.set(pos,temp);

                        //adapter.notifyDataSetChanged();
                        adapter.notifyItemChanged(pos);

                        return NotificationLike;
                    }
                });

    }

    public Observable<Friend> getFriendProfilePicObservable(final Friend friend){

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));
        HashMap<String, String> body = new HashMap<String, String>();

        return CommentsApi.getRequestApi()
                .getProfilePic(new HashMap<String, String>()
                {{
                    put("id",friend.getUser1().getId().toString());

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

                        NotificationsLinear temp = mapping.get(friend.getId().toString());
                        int pos = list.indexOf(temp);
                        temp.setProfilePicUrl(BASE_URL+profile_pic);
                        mapping.replace(friend.getId().toString(),temp);
                        list.set(pos,temp);

                        //adapter.notifyDataSetChanged();
                        adapter.notifyItemChanged(pos);

                        return friend;
                    }
                });

    }



    private Observable<Friend> getFriendRequestList(int frndReqPage){

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));

        return FriendsApi.getRequestApi()
                .getFriendRequestList(headerMap, frndReqPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Friend>, ObservableSource<Friend>>() {
                    @Override
                    public ObservableSource<Friend> apply(final List<Friend> friends) throws Exception {

                        for (Friend friend: friends) {

                            String[] datelist = friend.getCreatedDate().split("\\.");
                            String date ="date";
                            if(datelist.length > 1)
                                //System.out.println(datelist.length+"Count -----------"+datelist[0]);
                                date = datelist[0]+"Z";
                            else
                                date = friend.getCreatedDate();


                            NotificationsLinear temp = new NotificationsLinear(friend.getId().toString(),friend.getUser1().getId().toString(),friend.getUser1().getUsername(),"".toString(), friend.getUser1().getUsername()+" sent you a friend request","","",new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(date));
                            //mapping.put(likeN.getPostID().toString(),temp);
                            list.add(temp);
                        }

                        //adapter = new CustomListAdapter(NewsFeedActivity.this, R.layout.card_layout_main, list);
                        Collections.sort(list, Collections.reverseOrder());
                        mapping.clear();
                        for (NotificationsLinear n: list) {

                            //System.out.println(n.getDate().toString()+"||||||||"+n.getUsername());
                            mapping.put(n.getPostId(), n);

                        }

                        if(frndReqPage == 1){
                            adapter = new NotificationRecyclerView(mContext, R.layout.notification_linear_view, list, mapping);
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
