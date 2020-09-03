package com.connect.Search;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.connect.Auth.LoginActivity;
import com.connect.Auth.LogoutActivity;
import com.connect.Comments.CommentsApi;
import com.connect.Search.model.Search;
import com.connect.Search.model.SearchLinear;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
                    executeSearch(input);

                }

            }
        });




    }

    private void executeSearch(String input){


        getSearchObservable(input)
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


    private Observable<Search> getSearchObservable(String input){


        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));

        return SearchApi.getRequestApi()
                .getSearch(new HashMap<String, String>()
                {{
                    put("input", input);

                }},headerMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Search>, ObservableSource<Search>>() {
                    @Override
                    public ObservableSource<Search> apply(final List<Search> searches) throws Exception {

                        for (Search search: searches) {
                            System.out.println("Search: "+search.getUsername());
                            //data.put(f.getAuthor().getUsername(),"http://192.168.42.179:8000"+f.getPost_pics());
                            SearchLinear temp = new SearchLinear("drawable://" + R.drawable.arizona_dessert,search.getFirstName(), search.getLastName(), search.getId().toString());
                            mapping.put(search.getId().toString(),temp);
                            list.add(temp);
                        }

                        //adapter = new CustomListAdapter(NewsFeedActivity.this, R.layout.card_layout_main, list);
                        adapter = new SearchRecyclerView(mContext, R.layout.search_linear_view, list , mapping);
                        mListView.setAdapter(adapter);
                        mListView.setLayoutManager(new LinearLayoutManager(mContext));

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
                        temp.setProfile_pic("http://192.168.42.206:8000"+profile_pic);
                        mapping.replace(search.getId().toString(),temp);
                        list.set(pos,temp);

                        //adapter.notifyDataSetChanged();
                        adapter.notifyItemChanged(pos);

                        return search;
                    }
                });

    }


}
