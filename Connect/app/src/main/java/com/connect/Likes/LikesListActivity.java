package com.connect.Likes;

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
import com.connect.Likes.model.LikeList;
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


public class LikesListActivity extends AppCompatActivity {

    private static final String TAG = "LikesListActivity";

    SharedPreferences sharedpreferences;
    ArrayList<SearchLinear> list;
    HashMap<String, SearchLinear> mapping;
    private RecyclerView mListView;
    SearchRecyclerView adapter;
    private Context mContext;
    String BASE_URL;
    int page=1;
    HashSet<Integer> pageSet = new HashSet<>();
    LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes_list);

        //BASE_URL = "http://"+ getResources().getString(R.string.ip)+":8000";
        BASE_URL = "https://"+ getResources().getString(R.string.ip);

        sharedpreferences = getSharedPreferences("myKey", MODE_PRIVATE);

        mContext = com.connect.Likes.LikesListActivity.this;

        mListView = (RecyclerView) findViewById(R.id.likesList);

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLikesList);

        list = new ArrayList<>();
        mapping = new HashMap<>();

        Intent intent = getIntent();
        String id = intent.getStringExtra("post_id");

        executeLikeList(id, 1);
        pageSet.add(1);

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

                            if(!pageSet.contains(page)){
                                pageSet.add(page);
                                executeLikeList(id, page);
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
                pageSet.clear();
                executeLikeList(id, 1);
                pageSet.add(1);
                mSwipeRefreshLayout.setRefreshing(false);
                //adapter.notifyDataSetChanged();
            }



        });

    }

    private void executeLikeList(String id, int page){


        getLikesList(id,page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<LikeList, ObservableSource<LikeList>>() {
                    @Override
                    public ObservableSource<LikeList> apply(LikeList likeList) throws Exception {
                        return getProfilePicObservable(likeList);

                    }
                })
                .debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LikeList>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //disposables.add(d);
                    }

                    @Override
                    public void onNext(LikeList likeList) {
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


    private Observable<LikeList> getLikesList(String id, int page){


        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));

        return LikeApi.getRequestApi()
                .getLikeList(headerMap,Integer.parseInt(id),page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<LikeList>, ObservableSource<LikeList>>() {
                    @Override
                    public ObservableSource<LikeList> apply(final List<LikeList> likes) throws Exception {

                        for (LikeList like: likes) {
                            //System.out.println("Search: "+like.getPerson().getUsername());
                            //data.put(f.getAuthor().getUsername(),"http://192.168.42.179:8000"+f.getPost_pics());
                            SearchLinear temp = new SearchLinear("drawable://" + R.drawable.arizona_dessert,like.getPerson().getFirstName(), like.getPerson().getLastName(), like.getPerson().getId().toString());
                            mapping.put(like.getId().toString(),temp);
                            list.add(temp);
                        }
                        if(page == 1){
                            //adapter = new CustomListAdapter(NewsFeedActivity.this, R.layout.card_layout_main, list);
                            adapter = new SearchRecyclerView(mContext, R.layout.search_linear_view, list , mapping);
                            mListView.setAdapter(adapter);
                            linearLayoutManager = new LinearLayoutManager(mContext);
                            mListView.setLayoutManager(linearLayoutManager);
                        }
                        else{
                            adapter.notifyDataSetChanged();
                        }

                        //adapter.setPosts(posts);
                        //System.out.println(posts.get(0).getAuthor()+"---------");
                        return Observable.fromIterable(likes)
                                .subscribeOn(Schedulers.io());
                    }
                });
    }


    public Observable<LikeList> getProfilePicObservable(final LikeList likeList){

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));
        HashMap<String, String> body = new HashMap<String, String>();

        return CommentsApi.getRequestApi()
                .getProfilePic(new HashMap<String, String>()
                {{
                    put("id",likeList.getPerson().getId().toString());

                }},headerMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ResponseBody, LikeList>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public LikeList apply(ResponseBody profilePic) throws Exception {

                        JSONObject data = new JSONObject(profilePic.string());
                        String profile_pic = data.getString("profile_pic");
                        System.out.println("Profile Pic: "+profile_pic);
                        //post.setComments(comments);

                        SearchLinear temp = mapping.get(likeList.getId().toString());
                        int pos = list.indexOf(temp);
                        temp.setProfile_pic(BASE_URL+profile_pic);
                        mapping.replace(likeList.getId().toString(),temp);
                        list.set(pos,temp);

                        //adapter.notifyDataSetChanged();
                        adapter.notifyItemChanged(pos);

                        return likeList;
                    }
                });

    }


}
