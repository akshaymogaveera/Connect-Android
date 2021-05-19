package com.connect.UserProfileEdit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.connect.Home.ImageCaptureActivity;
import com.connect.UserProfileEdit.models.Info;
import com.connect.UserProfileEdit.models.UserProfile;
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


public class EditProfileFragment extends AppCompatActivity {

    Spinner s1,s2,s3;
    public static String BASE_URL;
    private static final String TAG = "EditProfileFragment";
    private EditText mDisplayName, mUsername, mWebsite, mDescription, mEmail, mPhoneNumber,mCity, mCountry, mSex;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;
    SharedPreferences sharedpreferences;
    String imgUrl, imgUrlOld;
    boolean profilePicChanged;
    Context mContext;
    int cityCount= 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        profilePicChanged = false;
        sharedpreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        mContext = EditProfileFragment.this;
        //BASE_URL = "http://"+ getResources().getString(R.string.ip)+":8000";
        BASE_URL = "https://"+ getResources().getString(R.string.ip);

        s1 = findViewById(R.id.spinner_country);
        s2 = findViewById(R.id.spinner_city);
        s3 = findViewById(R.id.spinner_sex);
        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                // TODO Auto-generated method stub
                String city[] = new String[0],sp1= String.valueOf(s1.getSelectedItem());
                if(sp1.toLowerCase().contentEquals("india")) {
                    city = getResources().getStringArray(R.array.city_india);
                }
                else if (sp1.toLowerCase().contentEquals("canada")){
                    city = getResources().getStringArray(R.array.city_canada);
                }
                else if (sp1.toLowerCase().contentEquals("usa")){
                    city = getResources().getStringArray(R.array.city_usa);
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, city);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dataAdapter.notifyDataSetChanged();
                s2.setAdapter(dataAdapter);
                s2.setSelection(cityCount);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getProfileData();
        //back arrow for navigating back to "ProfileActivity"
        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                finish();
            }
        });


        ImageView checkmark = (ImageView) findViewById(R.id.saveChanges);
        checkmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save changes.");
                try {
                    saveProfileData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //saveProfileSettings();
            }
        });

        mChangeProfilePhoto = (TextView) findViewById(R.id.changeProfilePhoto);
        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: changing profile photo");
                Intent intent = new Intent(EditProfileFragment.this, ImageCaptureActivity.class);
                intent.putExtra("imageViewId","profile_photo");
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
                //startActivity(intent);
                startActivityForResult(intent, 1);
                //getActivity().finish();
            }
        });




    }

    public void getProfileData(){


        mProfilePhoto = (CircleImageView) findViewById(R.id.profile_photo);
        mDisplayName = (EditText) findViewById(R.id.display_name);
        mUsername = (EditText) findViewById(R.id.username);
        //mCity = (EditText) findViewById(R.id.city);
        //mCountry = (EditText) findViewById(R.id.country);
        mEmail = (EditText) findViewById(R.id.email);
        mPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
        //mSex = (EditText) findViewById(R.id.sex);
        mChangeProfilePhoto = (TextView) findViewById(R.id.changeProfilePhoto);

        Retrofit userProfile = new Retrofit.Builder()
                .baseUrl(BASE_URL+"/firstapp/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserProfileAPI userProfileAPI = userProfile.create(UserProfileAPI.class);
        HashMap<String, String> headerMap = new HashMap<String, String>();
        HashMap<String, String> body = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));
        body.put("id",sharedpreferences.getString("id", null));
        Call<UserProfile> call = userProfileAPI.getData(body,headerMap);

        call.enqueue(new Callback<UserProfile>() {


            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                Log.d(TAG, "onResponse: Server Response: " + response.toString());
                Log.d(TAG, "onResponse: received information: " + response.errorBody());

                mUsername.setText(response.body().getInfo().getUser().getUsername());
                mUsername.setEnabled(false);
                mDisplayName.setText(response.body().getInfo().getUser().getFirstName()+" "+response.body().getInfo().getUser().getLastName());
                mDisplayName.setEnabled(false);
                mEmail.setText(response.body().getInfo().getUser().getEmail());
                String[] countries = getResources().getStringArray(R.array.country_arrays);
                int countryCount= 0;
                for(String country: countries){
                    if (country.toLowerCase().equals(response.body().getInfo().getCountry().toLowerCase())){
                        break;
                    }
                    countryCount++;
                }

                s1.setSelection(countryCount);

                String city[] = new String[0],sp1= String.valueOf(s1.getSelectedItem());
                if(sp1.toLowerCase().contentEquals("india")) {
                    city = getResources().getStringArray(R.array.city_india);
                }
                else if (sp1.toLowerCase().contentEquals("canada")){
                    city = getResources().getStringArray(R.array.city_canada);
                }
                else if (sp1.toLowerCase().contentEquals("usa")){
                    city = getResources().getStringArray(R.array.city_usa);
                }


                for(String c: city){
                    if (c.toLowerCase().equals(response.body().getInfo().getCity().toLowerCase())){
                        System.out.println(c+response.body().getInfo().getCity()+cityCount);
                        break;
                    }
                    cityCount++;
                }


                s2.setSelection(cityCount, false);

                if("male".equals(response.body().getInfo().getSex().toLowerCase())){
                    s3.setSelection(0);
                }else{
                    s3.setSelection(1);
                }
                s3.setEnabled(false);
                //mCity.setText(response.body().getInfo().getCity());
                //mCountry.setText(response.body().getInfo().getCountry());
                //mSex.setText(response.body().getInfo().getSex());
                //mSex.setEnabled(false);
                UniversalImageLoader.setImage(BASE_URL+response.body().getInfo().getProfilePic(), mProfilePhoto, null, "");
                imgUrl = response.body().getInfo().getProfilePic();
                System.out.println(response.body().getSelf());
                System.out.println(response.body().getInfo().getUser().getFirstName());
                //data.put(f.getAuthor().getUsername(),"http://192.168.42.179:8000"+f.getPost_pics());
                //list.add(new Card("http://192.168.42.206:8000"+f.getPost_pics(),f.getAuthor().getUsername()));

            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Log.e(TAG, "onFailure: Something went wrong: " + t.getMessage() );
                //Toast.makeText(Tab2Fragment.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void saveProfileData() throws IOException {

       String Email = mEmail.getText().toString();
       String City = String.valueOf(s2.getSelectedItem());
       String Country = String.valueOf(s1.getSelectedItem());

        Call<Info> call;


        Retrofit userProfile = new Retrofit.Builder()
                .baseUrl(BASE_URL+"/firstapp/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserProfileAPI userProfileAPI = userProfile.create(UserProfileAPI.class);
        HashMap<String, String> headerMap = new HashMap<String, String>();
        HashMap<String, String> body = new HashMap<String, String>();
        headerMap.put("Authorization", "Bearer "+sharedpreferences.getString("accessToken", null));

        RequestBody email = RequestBody.create(MediaType.parse("text/plain"),Email);
        RequestBody city = RequestBody.create(MediaType.parse("text/plain"),City);
        RequestBody country = RequestBody.create(MediaType.parse("text/plain"),Country);
        Map<String,RequestBody> bodyMap = new HashMap<>();

        bodyMap.put("email",email);
        bodyMap.put("city",city);
        bodyMap.put("country",country);
//        File file = new File(imgUrl);
        //RequestBody profile_pic = RequestBody.create(MediaType.parse("application/octet-stream"), file);

//        Bitmap mBitmap = MediaStore.Images.Media.getBitmap(EditProfileFragment.this.getContentResolver(), Uri.parse("file://"+imgUrl));
//
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100,stream);
//        //stream.toByteArray();

        //Bitmap original = BitmapFactory.decodeStream(getAssets().open("file://"+imgUrl));

        //mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        //Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));


        //MultipartBody.Part.createFormData("profile_pic","test.png",profile_pic);



        if(profilePicChanged){

            CompressImage mCompressImage = new CompressImage();

            Log.d(TAG,"Profile Pic Changed");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out = mCompressImage.compressImage(EditProfileFragment.this,imgUrl);

            RequestBody profile_pic = RequestBody.create(MediaType.parse("application/octet-stream"), out.toByteArray());
            String[] bits = imgUrl.split("/");
            String lastOne = bits[bits.length-1];
            call = userProfileAPI.saveProfileWithProfilePic(MultipartBody.Part.createFormData("profile_pic",lastOne,profile_pic),bodyMap, headerMap);
            //call = userProfileAPI.saveData(MultipartBody.Part.createFormData("profile_pic",lastOne,profile_pic),email, city, country, headerMap);
            profilePicChanged=false;
        }
        else{
            Log.d(TAG,"Profile Pic didn't Changed");
            call = userProfileAPI.saveProfileWithoutProfilePic(bodyMap, headerMap);
            //call = userProfileAPI.saveDataWithoutProfilePic(email, city, country, headerMap);
        }


        call.enqueue(new Callback<Info>() {


            @Override
            public void onResponse(Call<Info> call, Response<Info> response) {

                Log.d(TAG, "onResponse: Server Response: " + response.toString());
                //Log.d(TAG, "onResponse: received information: " + response.body().toString());
                String responseCode = String.valueOf(response.code());

                if (responseCode.contentEquals("200")) {

                    Log.d(TAG,"Email has Changed");
                    profilePicChanged=false;
                    sharedpreferences = mContext.getSharedPreferences("myKey",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    Log.d(TAG, "onResponse: received information: " );
                    mUsername.setText(response.body().getUser().getUsername());
                    mEmail.setText(response.body().getUser().getEmail());
                    editor.putString("profile_pic",response.body().getProfilePic());
                    editor.commit();
                    Log.d(TAG, "Profile Pic changes and saved locally" );
                    System.out.println(response.body());
                    AlertDialog alertDialog = new AlertDialog.Builder(EditProfileFragment.this).create();
                    alertDialog.setTitle("Success");
                    alertDialog.setMessage("Profile Updated !");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
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

                        if (error.has("email")){
                            JSONArray emailArray = new JSONArray(error.get("email").toString());
                            items.add(emailArray.getString(0));
                            UniversalImageLoader.setImage(BASE_URL+imgUrlOld, mProfilePhoto, null, "");
                            imgUrl = imgUrlOld;
                        }
                        else if (error.has("country")){
                            JSONArray emailArray = new JSONArray(error.get("country").toString());
                            items.add(emailArray.getString(0));
                            UniversalImageLoader.setImage(BASE_URL+imgUrlOld, mProfilePhoto, null, "");
                            imgUrl = imgUrlOld;
                        }
                        else if (error.has("city")){
                            JSONArray emailArray = new JSONArray(error.get("city").toString());
                            items.add(emailArray.getString(0));
                            UniversalImageLoader.setImage(BASE_URL+imgUrlOld, mProfilePhoto, null, "");
                            imgUrl = imgUrlOld;
                        }

//                        AlertDialog alertDialog = new AlertDialog.Builder(EditProfileFragment.this).create();
//                        alertDialog.setTitle("Alert");
                       // alertDialog.setMessage(errorMsg);
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileFragment.this);
                        builder.setTitle("Alert");
                        builder.setPositiveButton("OK", null);
                        builder.setItems(items.toArray(new CharSequence[items.size()]), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Do anything you want here
                            }
                        });
                        builder.create().show();

//                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                        alertDialog.show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //profilePicChanged=true;
                    //emailHasChanged = true;
                }
                else{
                    try {
                        Log.d(TAG, "onResponse: received information: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //profilePicChanged=true;
                    //emailHasChanged = true;
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
                imgUrlOld = imgUrl;
                imgUrl = data.getStringExtra("imgUrl");
                System.out.println("Old Image: "+imgUrlOld);
                System.out.println("New Image: "+imgUrl);
                UniversalImageLoader.setImage(imgUrl, mProfilePhoto, null,"");
                imgUrl = imgUrl.replace("file://","");
                profilePicChanged = true;

            }
        }
    }


}