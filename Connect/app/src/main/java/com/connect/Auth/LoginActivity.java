package com.connect.Auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.connect.Home.HomeActivity;
import com.connect.Register.RegisterActivity;
import com.connect.main.R;

import java.io.IOException;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    EditText username, pwd;
    Button login;
    private Context mContext;
    SharedPreferences sharedpreferences;
    TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = LoginActivity.this;
        Log.d(TAG, "Login: started.");
        username = findViewById(R.id.username);
        pwd = findViewById(R.id.pwd);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Register button clicked");
                Intent intent1 = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent1);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Login button clicked");
                String uName = username.getText().toString();
                String pass = pwd.getText().toString();
                try {
                    Validate.login(mContext,uName,pass);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void checkData(Context mContext,HashMap<String,String> data, String responseCode){

        LoginActivity login = new LoginActivity();
        if(responseCode.contentEquals("200")){

            login.makeToastWithActivity(mContext,true,data);

        }
        else{
            login.makeToastWithActivity(mContext,false, data);
        }

    }

    private void makeToastWithActivity(Context mContext,boolean action, HashMap<String,String> data){

        if(action){

            //sharedpreferences = getPreferences(Context.MODE_PRIVATE);
            sharedpreferences = mContext.getSharedPreferences("myKey",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("accessToken", data.get("accessToken"));
            editor.putString("refreshToken", data.get("refreshToken"));
            editor.putString("id", data.get("ID"));
            editor.commit();
            Log.d(TAG,"saved shared preference");
            Toast.makeText(mContext, "Login Successful", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(mContext, HomeActivity.class);
            mContext.startActivity(intent1);
            finish();

        }
        else {

            Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
        }
    }

}