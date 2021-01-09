package com.connect.Home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.connect.Auth.LoginActivity;
import com.connect.NewsFeed.NewsFeedFragment;
import com.connect.Notifications.NotificationActivity;
import com.connect.Post.CreatePostActivity;
import com.connect.Profile.ProfileActivity;
import com.connect.Search.SearchActivity;
import com.connect.main.R;
import com.connect.main.SectionsPageAdapter;
import com.connect.main.UniversalImageLoader;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HomeActivity extends AppCompatActivity {

    private static Context mContext;
    //private Context mContext = HomeActivity.this;
    private ViewPager mViewPager;
    static SharedPreferences sharedpreferences;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mContext = this;

//        Button btn = findViewById(R.id.logout);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SharedPreferences sharedpreferences = getSharedPreferences("myKey", MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedpreferences.edit();
//                editor.remove("id");
//                editor.commit();
//            }
//        });

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        sharedpreferences = getSharedPreferences("myKey",MODE_PRIVATE);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        //BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.hometoolbar);
        initImageLoader();



        toolbar.setOnMenuItemClickListener(new androidx.appcompat.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.logout_home:
                        SharedPreferences sharedpreferences = getSharedPreferences("myKey", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.remove("access");
                        editor.remove("id");
                        editor.commit();
                        Intent intent1 = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(intent1);
                        finish();
                        break;
                    // TODO: Other cases
                }
                return true;
            }
        });




        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
//                    case R.id.ic_arrow:
//                        break;

                    case R.id.ic_android:
                        Intent intent1 = new Intent(HomeActivity.this, SearchActivity.class);
                        startActivity(intent1);
                        return true;
                        //break;

                    case R.id.ic_books:
                        Intent intent2 = new Intent(HomeActivity.this, ProfileActivity.class);
                        startActivity(intent2);
                        return true;
//                        Intent intent2 = new Intent(MainActivity.this, ActivityTwo.class);
//                        startActivity(intent2);
//                        break;
//
                   case R.id.ic_center_focus:
                       Intent intent3 = new Intent(HomeActivity.this, CreatePostActivity.class);
                       startActivity(intent3);
                        return true;
//                        Intent intent3 = new Intent(MainActivity.this, ActivityThree.class);
//                        startActivity(intent3);
//                        break;
//
                    case R.id.ic_backup:
//                        Intent logout = new Intent(HomeActivity.this, LogoutActivity.class);
//                        startActivity(logout);
                        Intent noti = new Intent(HomeActivity.this, NotificationActivity.class);
                        startActivity(noti);
                        return true;

                }


                return false;
            }
        });

    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new NewsFeedFragment());
        viewPager.setAdapter(adapter);
    }

    public static Context getContext(){
        return mContext;
    }

    public static String getId(){
        return sharedpreferences.getString("id", null);
    }



}