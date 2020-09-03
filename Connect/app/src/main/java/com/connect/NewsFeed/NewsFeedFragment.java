package com.connect.NewsFeed;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.connect.Comments.CommentsApi;
import com.connect.NewsFeed.model.Feed;
import com.connect.main.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;

public class NewsFeedFragment extends Fragment {

    private static SharedPreferences sharedpreferencesobs;
    //private ListView mListView;
    private RecyclerView mListView;
    //CustomListAdapter adapter;
    NewsFeedRecyclerView adapter;
    private static final String TAG = "NewsFeedActivity";
    private static final String BASE_URL = "http://192.168.42.206:8000/firstapp/";
    public static SharedPreferences sharedpreferences;
    String countLikes, countComments, id, profileImgUrl;
    ArrayList<Card> list;
    HashMap<String, Card> mapping;
    boolean liked;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_news_feed,container,false);

        sharedpreferences = this.getActivity().getSharedPreferences("myKey", MODE_PRIVATE);
        sharedpreferencesobs = this.getActivity().getSharedPreferences("myKey", MODE_PRIVATE);
        //mListView = (ListView) findViewById(R.id.listView);
        mListView = (RecyclerView) view.findViewById(R.id.listView);
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh_newsfeed);
        countLikes = "0";
        countComments = "0";
        id ="NA";
        liked = false;
        list = new ArrayList<>();
        mapping = new HashMap<>();
//        list.add(new Card(id, "http://192.168.42.206:8000/media/post_pics/IMG-20181118-WA0138.jpg","Assam", countLikes, countComments, liked));
//        list.add(new Card(id, "drawable://" + R.drawable.arizona_dessert, "Arizona Dessert", countLikes, countComments, liked));


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to make your refresh action
                // CallYourRefreshingMethod();
                list.clear();
                mapping.clear();
                executeObservables();
                mSwipeRefreshLayout.setRefreshing(false);
                //adapter.notifyDataSetChanged();
            }



        });

        executeObservables();



        return view;
    }

    private void executeObservables(){

        getPostsObservable()
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

    private Observable<Feed> getPostsObservable(){

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));

        return NewsFeedApi.getRequestApi()
                .getObsData(headerMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Feed>, ObservableSource<Feed>>() {
                    @Override
                    public ObservableSource<Feed> apply(final List<Feed> posts) throws Exception {

                        for (Feed post: posts) {
                            System.out.println(post.getAuthor().getUsername());
                            //data.put(f.getAuthor().getUsername(),"http://192.168.42.179:8000"+f.getPost_pics());
                            Card temp = new Card(post.getAuthor().getId(), post.getId(), "http://192.168.42.206:8000"+post.getPost_pics(),post.getAuthor().getUsername(), countLikes, countComments, liked, post.getText(), "drawable://" + R.drawable.connect);
                            mapping.put(post.getId(),temp);
                            list.add(temp);
                        }

                        //adapter = new CustomListAdapter(NewsFeedActivity.this, R.layout.card_layout_main, list);
                        adapter = new NewsFeedRecyclerView(getActivity(), R.layout.card_layout_main, list , mapping);
                        mListView.setAdapter(adapter);
                        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
                        adapter.notifyItemChanged(pos);

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
                        adapter.notifyItemChanged(pos);

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
                        adapter.notifyItemChanged(pos);

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
                        temp.setProfileImgUrl("http://192.168.42.206:8000"+profile_pic);
                        mapping.replace(post.getId(),temp);
                        list.set(pos,temp);

                        //adapter.notifyDataSetChanged();
                        adapter.notifyItemChanged(pos);

                        return post;
                    }
                });

    }

}
