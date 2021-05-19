package com.connect.Register;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.connect.Auth.LoginActivity;
import com.connect.Home.ImageCaptureActivity;
import com.connect.UserProfileEdit.models.Info;
import com.connect.Utils.CompressImage;
import com.connect.main.R;
import com.connect.main.UniversalImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Context mContext = RegisterActivity.this;
    Spinner s1,s2,s3;
    public static String BASE_URL;
    private static final String TAG = "RegisterActivity";
    private EditText password, confirmPassword, mFirstName, mLastName, mUsername, mWebsite, mDescription, mEmail, mPhoneNumber;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;
    SharedPreferences sharedpreferences;
    String imgUrl="";
    Button register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //BASE_URL = "http://"+ getResources().getString(R.string.ip)+":8000";
        BASE_URL = "https://"+ getResources().getString(R.string.ip);
        sharedpreferences = getSharedPreferences("myKey", MODE_PRIVATE);

        s1 = findViewById(R.id.spinner_country);
        s2 = findViewById(R.id.spinner_city);
        s3 = findViewById(R.id.spinner_sex);
        s1.setOnItemSelectedListener(this);

        mProfilePhoto = findViewById(R.id.profile_photo);
        mFirstName = findViewById(R.id.first_name_register);
        mLastName = findViewById(R.id.last_name_register);
        mUsername =  findViewById(R.id.username);
        mEmail = findViewById(R.id.email);
        mPhoneNumber = findViewById(R.id.phoneNumber);
        mChangeProfilePhoto =  findViewById(R.id.changeProfilePhoto);
        password = findViewById(R.id.password_1);
        confirmPassword = findViewById(R.id.password_2);

        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to Home Activity");
                finish();
            }
        });
        register = findViewById(R.id.submitRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: create post called");

                if (!(mFirstName.getText().length() > 0)) {
                    Toast.makeText(mContext, "Please fill your First Name", Toast.LENGTH_SHORT).show();
                }
                else if (!(mLastName.getText().length() > 0)) {
                    Toast.makeText(mContext, "Please fill your Last Name", Toast.LENGTH_SHORT).show();
                }
                else if (!(mUsername.getText().length() > 4)) {
                    Toast.makeText(mContext, "Please fill your User Name", Toast.LENGTH_SHORT).show();
                }
                else if (!(String.valueOf(s2.getSelectedItem()).length() > 0)) {
                    Toast.makeText(mContext, "Please mention your City", Toast.LENGTH_SHORT).show();
                }
                else if (!(String.valueOf(s1.getSelectedItem()).length() > 0)) {
                    Toast.makeText(mContext, "Please mention your Country", Toast.LENGTH_SHORT).show();
                }
                else if (!(String.valueOf(s3.getSelectedItem()).length() > 0)) {
                    Toast.makeText(mContext, "Please fill your Gender", Toast.LENGTH_SHORT).show();
                }
                else if (!(password.getText().toString().length() > 0)) {
                    Toast.makeText(mContext, "Please fill your password", Toast.LENGTH_SHORT).show();
                }
                else if (!(confirmPassword.getText().toString().length() > 0)) {
                    Toast.makeText(mContext, "Please Re-Enter your password", Toast.LENGTH_SHORT).show();
                }
                else if (!(password.getText().toString().equals(confirmPassword.getText().toString()))) {
                    Toast.makeText(mContext, "Passwords don't match", Toast.LENGTH_SHORT).show();
                }
                else if (!(mEmail.getText().length() > 0)) {
                    Toast.makeText(mContext, "Please fill your Email ID", Toast.LENGTH_SHORT).show();
                }

                else if (imgUrl.isEmpty()) {
                    Toast.makeText(mContext, "Please choose an image", Toast.LENGTH_SHORT).show();
                }
                else {
                    System.out.println(mFirstName.getText().toString()+"|"+mLastName.getText().toString()+"|"+
                            mUsername.getText().toString()+"|"+mEmail.getText().toString());
                    Register();
                }


            }
        });

        mChangeProfilePhoto = (TextView) findViewById(R.id.changeProfilePhoto);
        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: changing profile photo");
                Intent intent = new Intent(mContext, ImageCaptureActivity.class);
                intent.putExtra("imageViewId","profile_photo");
                startActivityForResult(intent, 1);
            }
        });


    }

        public void Register(){

            CompressImage mCompressImage = new CompressImage();

            Call<Info> call;
            Retrofit register = new Retrofit.Builder()
                    .baseUrl(BASE_URL+"/firstapp/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RegisterAPI registerAPI = register.create(RegisterAPI.class);
            //HashMap<String, String> headerMap = new HashMap<String, String>();
            //HashMap<String, String> body = new HashMap<String, String>();
            //headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));

            Map<String,RequestBody> bodyMap = new HashMap<>();

            bodyMap.put("email",RequestBody.create(MediaType.parse("text/plain"),mEmail.getText().toString()));
            bodyMap.put("city",RequestBody.create(MediaType.parse("text/plain"),String.valueOf(s2.getSelectedItem())));
            bodyMap.put("country",RequestBody.create(MediaType.parse("text/plain"),String.valueOf(s1.getSelectedItem())));
            bodyMap.put("first_name",RequestBody.create(MediaType.parse("text/plain"),mFirstName.getText().toString()));
            bodyMap.put("last_name",RequestBody.create(MediaType.parse("text/plain"),mLastName.getText().toString()));
            bodyMap.put("username",RequestBody.create(MediaType.parse("text/plain"),mUsername.getText().toString()));
            bodyMap.put("sex",RequestBody.create(MediaType.parse("text/plain"),String.valueOf(s3.getSelectedItem())));
            bodyMap.put("password",RequestBody.create(MediaType.parse("text/plain"),password.getText().toString()));


            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out = mCompressImage.compressImage(mContext,imgUrl);

            RequestBody profile_pic = RequestBody.create(MediaType.parse("application/octet-stream"), out.toByteArray());
            String[] bits = imgUrl.split("/");
            String lastOne = bits[bits.length-1];
            call = registerAPI.saveProfileWithProfilePic(MultipartBody.Part.createFormData("profile_pic",lastOne,profile_pic),bodyMap);

            call.enqueue(new Callback<Info>() {

                @Override
                public void onResponse(Call<Info> call, Response<Info> response) {

                    Log.d(TAG, "onResponse: Server Response: " + response.toString());
                    //Log.d(TAG, "onResponse: received information: " + response.body().toString());
                    String responseCode = String.valueOf(response.code());

                    if (responseCode.contentEquals("200")) {

                        Log.d(TAG,"Email has Changed");

                        Log.d(TAG, "onResponse: received information: " );
                        mUsername.setText(response.body().getUser().getUsername());
                        mEmail.setText(response.body().getUser().getEmail());
                        System.out.println(response.body());
                        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                        alertDialog.setTitle("Success");
                        alertDialog.setMessage("Account Created !");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent intent1 = new Intent(mContext, LoginActivity.class);
                                        startActivity(intent1);
                                        finish();
                                    }
                                });
                        alertDialog.show();


                    }
                    else if (responseCode.contentEquals("400")){
                        List<String> items = new ArrayList<String>();
                        try {

                            //Log.d(TAG, "onResponse: received information: " + response.errorBody().string());
                            JSONObject errorResponse = new JSONObject(response.errorBody().string());
                            JSONObject error = errorResponse.getJSONObject("error");

                            if (error.has("username")){
                                JSONArray emailArray = new JSONArray(error.get("username").toString());
                                items.add(emailArray.getString(0));
                            }
                            else if (error.has("password")){
                                JSONArray emailArray = new JSONArray(error.get("password").toString());
                                items.add(emailArray.getString(1));
                            }
                            else if (error.has("first_name")){
                                JSONArray emailArray = new JSONArray(error.get("first_name").toString());
                                items.add(emailArray.getString(1));
//                                UniversalImageLoader.setImage(BASE_URL+imgUrlOld, mProfilePhoto, null, "");
//                                imgUrl = imgUrlOld;
                            }
                            else if (error.has("last_name")){
                                JSONArray emailArray = new JSONArray(error.get("last_name").toString());
                                items.add(emailArray.getString(1));
//                                UniversalImageLoader.setImage(BASE_URL+imgUrlOld, mProfilePhoto, null, "");
//                                imgUrl = imgUrlOld;
                            }
                            else if (error.has("email")){
                                JSONArray emailArray = new JSONArray(error.get("email").toString());
                                items.add(emailArray.getString(0));
//                                UniversalImageLoader.setImage(BASE_URL+imgUrlOld, mProfilePhoto, null, "");
//                                imgUrl = imgUrlOld;
                            }
                            else if (error.has("country")){
                                JSONArray emailArray = new JSONArray(error.get("country").toString());
                                items.add(emailArray.getString(0));
//                                UniversalImageLoader.setImage(BASE_URL+imgUrlOld, mProfilePhoto, null, "");
//                                imgUrl = imgUrlOld;
                            }
                            else if (error.has("city")){
                                JSONArray emailArray = new JSONArray(error.get("city").toString());
                                items.add(emailArray.getString(0));
                            }
                            else{
                                items.add("Some error occured!, Please try later.");
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Alert");
                            builder.setPositiveButton("OK", null);
                            builder.setItems(items.toArray(new CharSequence[items.size()]), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do anything you want here
                                }
                            });
                            builder.create().show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    else{
                        try {
                            Log.d(TAG, "onResponse: received information: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(Call<Info> call, Throwable t) {
                    Log.e(TAG, "onFailure: Something went wrong: " + t.getMessage() );
                    //Toast.makeText(Tab2Fragment.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });

        }



    @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1) {
                if(resultCode == RESULT_OK) {
                    imgUrl = data.getStringExtra("imgUrl");
                    System.out.println("New Image: "+imgUrl);
                    UniversalImageLoader.setImage(imgUrl, mProfilePhoto, null,"");
                    imgUrl = imgUrl.replace("file://","");

                }
            }
        }




    
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {
        // TODO Auto-generated method stub
        String city[] = new String[0],sp1= String.valueOf(s1.getSelectedItem());
        if(sp1.contentEquals("India")) {
            city = getResources().getStringArray(R.array.city_india);
        }
        else if (sp1.contentEquals("Canada")){
            city = getResources().getStringArray(R.array.city_canada);
        }
        else if (sp1.contentEquals("USA")){
            city = getResources().getStringArray(R.array.city_usa);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, city);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.notifyDataSetChanged();
        s2.setAdapter(dataAdapter);


    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

}