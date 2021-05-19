package com.connect.Comments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.connect.Comments.model.CommentLinear;
import com.connect.Comments.model.Comments;
import com.connect.NewsFeed.NewsFeedFragment;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAllCommentsActivity extends AppCompatActivity {

    private static final String TAG = "ViewAllCommentsActivity";

    SharedPreferences sharedpreferences;
    ArrayList<CommentLinear> list;
    HashMap<String, CommentLinear> mapping;
    private RecyclerView mListView;
    CommentsRecyclerView adapter;
    private Context mContext;
    EditText commentContent;
    String BASE_URL, postAuthorId, postId, userId, userName;
    int page=1;
    HashSet<Integer> pageSet = new HashSet<>();
    LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_comments);

        //BASE_URL = "http://"+ getResources().getString(R.string.ip)+":8000";
        BASE_URL = "https://"+ getResources().getString(R.string.ip);
        sharedpreferences = getSharedPreferences("myKey", MODE_PRIVATE);

        mContext = ViewAllCommentsActivity.this;

        mListView = (RecyclerView) findViewById(R.id.commentRecycleList);

        commentContent = (EditText) findViewById(R.id.commentText);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshCommentsList);
        toolbar = findViewById(R.id.commentToolBar);
        list = new ArrayList<>();
        mapping = new HashMap<>();

        Intent intent = getIntent();
        postId = intent.getStringExtra("post_id");
        postAuthorId = intent.getStringExtra("authorId");
        userId = sharedpreferences.getString("id", null);
        userName = sharedpreferences.getString("userName", null);

        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to Home Activity");
                finish();
            }
        });

        ImageView saveChanges = (ImageView) findViewById(R.id.saveChanges);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating Save changes");
                addComment(postId);
                commentContent.setText("");

            }
        });


        toolbar.setOnMenuItemClickListener(new androidx.appcompat.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.delete:
                        int count =0;
                        for(CommentLinear l :list){
                            if(l.isSelected()){
                                Log.d(TAG, "Comment --"+l.getText());
                                deleteComment(l);
                                l.setSelected(false);
                                adapter.notifyDataSetChanged();
                            }
                            else{
                                count++;
                            }
                        }



                        if(count == list.size()){
                            Toast.makeText(mContext, "No comment selected!", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    // TODO: Other cases
                }
                return true;
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
                                executeObservables(postId, page);
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
                executeObservables(postId, 1);
                pageSet.add(1);
                mSwipeRefreshLayout.setRefreshing(false);
                //adapter.notifyDataSetChanged();
            }



        });

        executeObservables(postId,1);
        pageSet.add(1);

    }

    private void executeObservables(String id, int page){


        getCommentsObservable(id,page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Comments, ObservableSource<Comments>>() {
                    @Override
                    public ObservableSource<Comments> apply(Comments comments) throws Exception {
                        return getProfilePicObservable(comments);

                    }
                })
                .debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Comments>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //disposables.add(d);
                    }

                    @Override
                    public void onNext(Comments comments) {
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

    private Observable<Comments> getCommentsObservable(String id, int page){


        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));

        return CommentsApi.getRequestApi()
                .getCommentsList(headerMap,Integer.parseInt(id), page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Comments>, ObservableSource<Comments>>() {
                    @Override
                    public ObservableSource<Comments> apply(final List<Comments> comments) throws Exception {

                        for (Comments comment: comments) {
                            System.out.println("Comment: "+comment.getAuthor().getUsername()+" | "+comment.getText()+" | "+comment.getPost());
                            //data.put(f.getAuthor().getUsername(),"http://192.168.42.179:8000"+f.getPost_pics());
                            CommentLinear temp = new CommentLinear("drawable://" + R.drawable.arizona_dessert,comment.getAuthor().getUsername(), comment.getText(), comment.getCreatedDate(), comment.getId());
                            mapping.put(comment.getId().toString(),temp);
                            list.add(temp);
                        }

                        if(page == 1){

                            //adapter = new CustomListAdapter(NewsFeedActivity.this, R.layout.card_layout_main, list);
                            adapter = new CommentsRecyclerView(mContext, R.layout.comment_linear_view, list , mapping, postAuthorId, userId, userName);
                            mListView.setAdapter(adapter);
                            linearLayoutManager = new LinearLayoutManager(mContext);
                            mListView.setLayoutManager(linearLayoutManager);

                        }
                        else{
                            adapter.notifyDataSetChanged();
                        }

                        //adapter.setPosts(posts);
                        //System.out.println(posts.get(0).getAuthor()+"---------");
                        return Observable.fromIterable(comments)
                                .subscribeOn(Schedulers.io());
                    }
                });
    }


    public Observable<Comments> getProfilePicObservable(final Comments comments){

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));
        HashMap<String, String> body = new HashMap<String, String>();

        return CommentsApi.getRequestApi()
                .getProfilePic(new HashMap<String, String>()
                {{
                    put("id",comments.getAuthor().getId());

                }},headerMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ResponseBody, Comments>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public Comments apply(ResponseBody profilePic) throws Exception {

                        JSONObject data = new JSONObject(profilePic.string());
                        String profile_pic = data.getString("profile_pic");
                        System.out.println("Profile Pic: "+profile_pic);
                        //post.setComments(comments);

                        CommentLinear temp = mapping.get(comments.getId().toString());
                        int pos = list.indexOf(temp);
                        temp.setProfile_pic(BASE_URL+profile_pic);
                        mapping.replace(comments.getId().toString(),temp);
                        list.set(pos,temp);

                        //adapter.notifyDataSetChanged();
                        adapter.notifyItemChanged(pos);

                        return comments;
                    }
                });

    }

    public void addComment(String id){

        String comment = commentContent.getText().toString();

        if(comment.isEmpty() || !(comment.length()>0) || comment.equals("")){
            Log.d(TAG,"Invalid Comment");
        }
        else {


            CommentsApi addCommentApi = CommentsApi.getRequestApi();
            HashMap<String, String> headerMap = new HashMap<String, String>();
            headerMap.put("Authorization", "Bearer " + NewsFeedFragment.sharedpreferences.getString("accessToken", null));
            headerMap.put("Content-Type", "application/json");

            HashMap<String, String> body = new HashMap<String, String>();
            body.put("id", id);
            body.put("text",comment);

            Call<Comments> call = addCommentApi.addComment(body, headerMap);

            call.enqueue(new Callback<Comments>() {
                @Override
                public void onResponse(Call<Comments> call, Response<Comments> response) {
                    Log.d(TAG, "onResponse: Server Response: " + response.toString());

                    String responseCode = String.valueOf(response.code());
                    Log.d(TAG, "onResponse: json: " + responseCode);
                    //JSONObject data = null;
                    // data = new JSONObject(json);
                    //                         //   Log.d(TAG, "onResponse: data: " + data.optString("json"));
                    if (responseCode.contentEquals("200")) {

                        try {

                            Log.d(TAG, "Comment Added " + responseCode);
                            CommentLinear temp = new CommentLinear(BASE_URL+NewsFeedFragment.sharedpreferences.getString("profile_pic", null),response.body().getAuthor().getUsername(), response.body().getText(), response.body().getCreatedDate(), response.body().getId());
                            mapping.put(response.body().getId().toString(),temp);
                            list.add(0,temp);
                            adapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else {
                        Log.d(TAG, "Post not Liked " + responseCode);
                    }

                }

                @Override
                public void onFailure(Call<Comments> call, Throwable t) {
                    Log.e(TAG, "onFailure: Something went wrong: " + t.getMessage());

                }
            });
        }

    }

    public void deleteComment(CommentLinear commentLinear){


        CommentsApi commentsApi = CommentsApi.getRequestApi();
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer " + NewsFeedFragment.sharedpreferences.getString("accessToken", null));
        headerMap.put("Content-Type", "application/json");

        HashMap<String, Integer> body = new HashMap<String, Integer>();
        body.put("id", commentLinear.getId());

        Call<Comments> call = commentsApi.deleteComment(body, headerMap);

        call.enqueue(new Callback<Comments>() {
            @Override
            public void onResponse(Call<Comments> call, Response<Comments> response) {
                Log.d(TAG, "onResponse: Server Response: " + response.toString());

                String responseCode = String.valueOf(response.code());
                Log.d(TAG, "onResponse: json: " + responseCode);
                //JSONObject data = null;
                // data = new JSONObject(json);
                //                         //   Log.d(TAG, "onResponse: data: " + data.optString("json"));
                if (responseCode.contentEquals("200")) {

                    try {


                        Log.d(TAG, "Comment Deleted " + responseCode);
                        Toast.makeText(mContext, "Comment Deleted ", Toast.LENGTH_SHORT).show();
                        list.remove(commentLinear);
                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    Log.d(TAG, "Comment not delete " + responseCode);
                    Toast.makeText(mContext, "Unauthorized to delete comment!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Comments> call, Throwable t) {
                Log.e(TAG, "onFailure: Something went wrong: " + t.getMessage());

            }
        });


    }
}