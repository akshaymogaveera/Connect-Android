package com.connect.Search;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.connect.Comments.CommentsApi;
import com.connect.Search.model.Search;
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

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";

    SharedPreferences sharedpreferences;
    ArrayList<SearchLinear> list;
    HashMap<String, SearchLinear> mapping;
    private RecyclerView mListView;
    SearchRecyclerView adapter;
    private Context mContext;
    EditText searchText;
    Button searchButton;
    String BASE_URL;
    int page=1;
    HashSet<Integer> pageSet = new HashSet<>();
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //BASE_URL = "http://"+ getResources().getString(R.string.ip)+":8000";
        BASE_URL = "https://"+ getResources().getString(R.string.ip);

        sharedpreferences = getSharedPreferences("myKey", MODE_PRIVATE);

        mContext = SearchActivity.this;

        mListView = (RecyclerView) findViewById(R.id.searchList);

        searchText = (EditText) findViewById(R.id.searchText);

        searchButton = (Button) findViewById(R.id.searchButtonTop);

        list = new ArrayList<>();
        mapping = new HashMap<>();


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: search clicked");

                String input = searchText.getText().toString();

                if (input.isEmpty() || input.length() < 2){

                    Log.d(TAG,"invalid search");
                    Toast.makeText(mContext, "Invalid Search", Toast.LENGTH_SHORT).show();

                }
                else{
                    list.clear();
                    mapping.clear();
                    executeSearch(input, 1);
                    pageSet.add(1);

                }

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
                            page = (totalItemCount[0]/14)+1;
                            Log.v("...", "Page "+page);

                            if(!pageSet.contains(page)){
                                pageSet.add(page);

                                String input = searchText.getText().toString();

                                if (input.isEmpty() || input.length() < 2){

                                    Log.d(TAG,"invalid search");
                                    Toast.makeText(mContext, "Invalid Search", Toast.LENGTH_SHORT).show();

                                }
                                else{
                                    executeSearch(input, page);

                                }
                            }

                            loading[0] = true;
                        }
                    }
                }
            }
        });




    }

    private void executeSearch(String input, int page){


        getSearchObservable(input, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Search, ObservableSource<Search>>() {
                    @Override
                    public ObservableSource<Search> apply(Search search) throws Exception {
                        return getProfilePicObservable(search);

                    }
                })
                .debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Search>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //disposables.add(d);
                    }

                    @Override
                    public void onNext(Search comments) {
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


    private Observable<Search> getSearchObservable(String input,int page){


        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));

        return SearchApi.getRequestApi()
                .getSearch(headerMap, input, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Search>, ObservableSource<Search>>() {
                    @Override
                    public ObservableSource<Search> apply(final List<Search> searches) throws Exception {

                        if (searches.isEmpty()){

                            Log.d(TAG,"No match found!");
                            Toast.makeText(mContext, "No match found!", Toast.LENGTH_SHORT).show();
                        }

                        for (Search search: searches) {
                            System.out.println("Search: "+search.getUsername());
                            //data.put(f.getAuthor().getUsername(),"http://192.168.42.179:8000"+f.getPost_pics());
                            SearchLinear temp = new SearchLinear("drawable://" + R.drawable.arizona_dessert,search.getFirstName(), search.getLastName(), search.getId().toString());
                            mapping.put(search.getId().toString(),temp);
                            list.add(temp);
                        }

                        if(page == 1){

                            //adapter = new CustomListAdapter(NewsFeedActivity.this, R.layout.card_layout_main, list);
                            adapter = new SearchRecyclerView(mContext, R.layout.search_linear_view, list , mapping);
                            mListView.setAdapter(adapter);
                            mListView.setLayoutManager(new LinearLayoutManager(mContext));

                        }
                        else{
                            adapter.notifyDataSetChanged();
                        }

                        //adapter.setPosts(posts);
                        //System.out.println(posts.get(0).getAuthor()+"---------");
                        return Observable.fromIterable(searches)
                                .subscribeOn(Schedulers.io());
                    }
                });
    }


    public Observable<Search> getProfilePicObservable(final Search search){

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));
        HashMap<String, String> body = new HashMap<String, String>();

        return CommentsApi.getRequestApi()
                .getProfilePic(new HashMap<String, String>()
                {{
                    put("id",search.getId().toString());

                }},headerMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ResponseBody, Search>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public Search apply(ResponseBody profilePic) throws Exception {

                        JSONObject data = new JSONObject(profilePic.string());
                        String profile_pic = data.getString("profile_pic");
                        System.out.println("Profile Pic: "+profile_pic);
                        //post.setComments(comments);

                        SearchLinear temp = mapping.get(search.getId().toString());
                        int pos = list.indexOf(temp);
                        temp.setProfile_pic(BASE_URL+profile_pic);
                        mapping.replace(search.getId().toString(),temp);
                        list.set(pos,temp);

                        //adapter.notifyDataSetChanged();
                        adapter.notifyItemChanged(pos);

                        return search;
                    }
                });

    }


}
