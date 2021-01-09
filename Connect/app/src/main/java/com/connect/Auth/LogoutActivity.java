package com.connect.Auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.connect.Notifications.NotificationActivity;
import com.connect.main.R;

public class LogoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        Button logoutbtn = findViewById(R.id.logoutbtn);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedpreferences = getSharedPreferences("myKey", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.remove("access");
                editor.remove("id");
                editor.commit();
                Intent intent1 = new Intent(LogoutActivity.this, LoginActivity.class);
                startActivity(intent1);
                finish();
            }
        });

        Button noti = findViewById(R.id.notifications);
        noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent noti = new Intent(LogoutActivity.this, NotificationActivity.class);
                startActivity(noti);
                finish();
            }
        });


    }
}