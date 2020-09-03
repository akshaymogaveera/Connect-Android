package com.connect.Notifications;


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
import com.connect.NewsFeed.Card;
import com.connect.NewsFeed.NewsFeedApi;
import com.connect.NewsFeed.NewsFeedRecyclerView;
import com.connect.NewsFeed.model.Feed;
import com.connect.Search.SearchApi;
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

public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = "NotificationActivity";

    SharedPreferences sharedpreferences;
    ArrayList<SearchLinear> list;
    HashMap<String, SearchLinear> mapping;
    private RecyclerView mListView;
    NotificationRecyclerView adapter;
    private Context mContext;
    EditText searchText;
    Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        sharedpreferences = getSharedPreferences("myKey", MODE_PRIVATE);

        mContext = NotificationActivity.this;

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


                }

            }
        });




    }



}
