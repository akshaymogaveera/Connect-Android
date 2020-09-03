//package com.connect.NewsFeed;
//
//import android.content.SharedPreferences;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.connect.NewsFeed.model.Feed;
//import com.connect.main.R;
//
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import io.reactivex.Observable;
//import io.reactivex.ObservableSource;
//import io.reactivex.Observer;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Function;
//import io.reactivex.schedulers.Schedulers;
//import okhttp3.ResponseBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class NewsFeedActivity extends AppCompatActivity {
//
//    private static SharedPreferences sharedpreferencesobs;
//    //private ListView mListView;
//    private RecyclerView mListView;
//    //CustomListAdapter adapter;
//    NewsFeedRecyclerView adapter;
//    private static final String TAG = "NewsFeedActivity";
//    private static final String BASE_URL = "http://192.168.42.206:8000/firstapp/";
//    SharedPreferences sharedpreferences;
//    String countLikes, countComments, id, caption;
//    ArrayList<Card> list;
//    HashMap<String, Card> mapping;
//    boolean liked;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_news_feed);
//        sharedpreferences = getSharedPreferences("myKey", MODE_PRIVATE);
//        sharedpreferencesobs = getSharedPreferences("myKey", MODE_PRIVATE);
//        //mListView = (ListView) findViewById(R.id.listView);
//        mListView = (RecyclerView) findViewById(R.id.listView);
//        countLikes = "NA";
//        countComments = "NA";
//        liked = false;
//        list = new ArrayList<>();
//        mapping = new HashMap<>();
//        id = "NA";
//        caption = "NA";
//        list.add(new Card(id, "http://192.168.42.206:8000/media/post_pics/IMG-20181118-WA0138.jpg","Assam", countLikes, countComments, liked, caption));
//        list.add(new Card(id, "drawable://" + R.drawable.arizona_dessert, "Arizona Dessert", countLikes, countComments, liked, caption));
//        list.add(new Card(id, "drawable://" + R.drawable.colorado_mountains, "Colorado Mountains", countLikes, countComments, liked, caption));
//        list.add(new Card(id, "drawable://" + R.drawable.hawaii_rainforest, "DavenPort California", countLikes, countComments, liked, caption));
////        list.add(new Card("drawable://" + R.drawable.newfoundland_ice, "NewFoundLand Ice"));
////        list.add(new Card("drawable://" + R.drawable.arizona_dessert, "Arizona Dessert"));
////        list.add(new Card("drawable://" + R.drawable.colorado_mountains, "Colorado Mountains"));
////        list.add(new Card("drawable://" + R.drawable.hawaii_rainforest, "DavenPort California"));
////        list.add(new Card("drawable://" + R.drawable.newfoundland_ice, "NewFoundLand Ice"));
////        list.add(new Card("drawable://" + R.drawable.arizona_dessert, "Arizona Dessert"));
////        list.add(new Card("drawable://" + R.drawable.colorado_mountains, "Colorado Mountains"));
////        list.add(new Card("drawable://" + R.drawable.newfoundland_ice, "DavenPort California"));
////        list.add(new Card("drawable://" + R.drawable.hawaii_rainforest, "DavenPort California"));
//        //getNewsFeed(list);
//
//
//
//        getPostsObservable()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .flatMap(new Function<Feed, ObservableSource<Feed>>() {
//                    @Override
//                    public ObservableSource<Feed> apply(Feed post) throws Exception {
//                        return getLikesObservable(post);
//
//                    }
//                })
//                .flatMap(new Function<Feed, ObservableSource<Feed>>() {
//                    @Override
//                    public ObservableSource<Feed> apply(Feed post) throws Exception {
//                        return getCommentsObservable(post);
//
//                    }
//                })
//                .debounce(400, TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Feed>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        //disposables.add(d);
//                    }
//
//                    @Override
//                    public void onNext(Feed post) {
//                        //updatePost(post);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e(TAG, "onError: ", e);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                    }
//                });
//
//
//    }
//
//
//
//    private Observable<Feed> getPostsObservable(){
//
//        HashMap<String, String> headerMap = new HashMap<String, String>();
//        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));
//
//        return NewsFeedApi.getRequestApi()
//                .getObsData(headerMap)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .flatMap(new Function<List<Feed>, ObservableSource<Feed>>() {
//                    @Override
//                    public ObservableSource<Feed> apply(final List<Feed> posts) throws Exception {
//
//                        for (Feed f: posts) {
//                            System.out.println(f.getAuthor().getUsername());
//                            //data.put(f.getAuthor().getUsername(),"http://192.168.42.179:8000"+f.getPost_pics());
//                            Card temp = new Card(id, "http://192.168.42.206:8000"+f.getPost_pics(),f.getAuthor().getUsername(), countLikes, countComments, liked, caption);
//                            mapping.put(f.getId(),temp);
//                            list.add(temp);
//                        }
//
//                        //adapter = new CustomListAdapter(NewsFeedActivity.this, R.layout.card_layout_main, list);
//                        adapter = new NewsFeedRecyclerView(NewsFeedActivity.this, R.layout.card_layout_main, list, mapping);
//                        mListView.setAdapter(adapter);
//                        mListView.setLayoutManager(new LinearLayoutManager(NewsFeedActivity.this));
//
//                        //adapter.setPosts(posts);
//                        System.out.println(posts.get(0).getAuthor()+"---------");
//                        return Observable.fromIterable(posts)
//                                .subscribeOn(Schedulers.io());
//                    }
//                });
//    }
//
//
//    private Observable<Feed> getLikesObservable(final Feed post){
//
//        HashMap<String, String> headerMap = new HashMap<String, String>();
//        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));
//        HashMap<String, String> body = new HashMap<String, String>();
//
//        return NewsFeedApi.getRequestApi()
//                .countLikesObs(new HashMap<String, String>()
//                {{
//                    put("id", post.getId());
//
//                }},headerMap)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .map(new Function<ResponseBody, Feed>() {
//                    @RequiresApi(api = Build.VERSION_CODES.N)
//                    @Override
//                    public Feed apply(ResponseBody comments) throws Exception {
//
//                        JSONObject data = new JSONObject(comments.string());
//                        String count = data.getString("count");
//                        System.out.println(count+" Ccunt -------------"+post.getId());
//                        //post.setComments(comments);
//
//                        Card temp = mapping.get(post.getId());
//                        int pos = list.indexOf(temp);
//                        temp.setCountLikes(count);
//                        mapping.replace(post.getId(),temp);
//                        list.set(pos,temp);
//
//                        //adapter.notifyDataSetChanged();
//                        adapter.notifyItemChanged(pos);
//
//                        return post;
//                    }
//                });
//
//    }
//
//    private Observable<Feed> getCommentsObservable(final Feed post){
//
//        HashMap<String, String> headerMap = new HashMap<String, String>();
//        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));
//        HashMap<String, String> body = new HashMap<String, String>();
//
//        return NewsFeedApi.getRequestApi()
//                .countLikesObs(new HashMap<String, String>()
//                {{
//                    put("id", post.getId());
//
//                }},headerMap)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .map(new Function<ResponseBody, Feed>() {
//                    @RequiresApi(api = Build.VERSION_CODES.N)
//                    @Override
//                    public Feed apply(ResponseBody comments) throws Exception {
//
//                        JSONObject data = new JSONObject(comments.string());
//                        String count = data.getString("count");
//                        System.out.println(count+" Ccunt -------------"+post.getId());
//                        //post.setComments(comments);
//
//                        Card temp = mapping.get(post.getId());
//                        int pos = list.indexOf(temp);
//                        temp.setCountComments(count);
//                        mapping.replace(post.getId(),temp);
//                        list.set(pos,temp);
//
//                        //adapter.notifyDataSetChanged();
//                        adapter.notifyItemChanged(pos);
//
//                        return post;
//                    }
//                });
//
//    }
//
//
//    private void getNewsFeed(ArrayList<Card> list){
//
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        NewsFeedApi redditAPI = retrofit.create(NewsFeedApi.class);
//        HashMap<String, String> headerMap = new HashMap<String, String>();
//        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));
//        Call<List<Feed>> call = redditAPI.getData(headerMap);
//
//        call.enqueue(new Callback<List<Feed>>() {
//
//
//            @Override
//            public void onResponse(Call<List<Feed>> call, Response<List<Feed>> response) {
//                Log.d(TAG, "onResponse: Server Response: " + response.toString());
//                Log.d(TAG, "onResponse: received information: " + response.toString());
//
////                for (ListIterator<Feed> it = response.body().listIterator(); it.hasNext(); ) {
////                    Feed f = it.next();
////                    System.out.println(f.getAuthor().getUsername());
////                    //data.put(f.getAuthor().getUsername(),"http://192.168.42.179:8000"+f.getPost_pics());
////                    Card temp = new Card("http://192.168.42.206:8000"+f.getPost_pics(),f.getAuthor().getUsername(), countLikes, countComments);
////                    mapping.put(f.getId(),temp);
////                    list.add(temp);
////                }
////
////                adapter = new CustomListAdapter(NewsFeedActivity.this, R.layout.card_layout_main, list);
////                mListView.setAdapter(adapter);
//            }
//
//            @Override
//            public void onFailure(Call<List<Feed>> call, Throwable t) {
//                Log.e(TAG, "onFailure: Something went wrong: " + t.getMessage() );
//                //Toast.makeText(Tab2Fragment.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}