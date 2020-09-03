package com.connect.UserProfileEdit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.connect.Home.ImageCaptureActivity;
import com.connect.UserProfileEdit.models.Info;
import com.connect.UserProfileEdit.models.UserProfile;
import com.connect.main.R;
import com.connect.main.UniversalImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

    public static String Url = "http://192.168.42.206:8000";
    private static final String TAG = "EditProfileFragment";
    private EditText mDisplayName, mUsername, mWebsite, mDescription, mEmail, mPhoneNumber,mCity, mCountry, mSex;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;
    SharedPreferences sharedpreferences;
    String imgUrl;
    boolean profilePicChanged;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        profilePicChanged = false;
        sharedpreferences = getSharedPreferences("myKey", MODE_PRIVATE);



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
        mCity = (EditText) findViewById(R.id.city);
        mCountry = (EditText) findViewById(R.id.country);
        mEmail = (EditText) findViewById(R.id.email);
        mPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
        mSex = (EditText) findViewById(R.id.sex);
        mChangeProfilePhoto = (TextView) findViewById(R.id.changeProfilePhoto);

        Retrofit userProfile = new Retrofit.Builder()
                .baseUrl(Url+"/firstapp/")
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
                mCity.setText(response.body().getInfo().getCity());
                mCountry.setText(response.body().getInfo().getCountry());
                mSex.setText(response.body().getInfo().getSex());
                mSex.setEnabled(false);
                UniversalImageLoader.setImage(Url+response.body().getInfo().getProfilePic(), mProfilePhoto, null, "");
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
       String City = mCity.getText().toString();
       String Country = mCountry.getText().toString();

        Call<Info> call;


        Retrofit userProfile = new Retrofit.Builder()
                .baseUrl(Url+"/firstapp/")
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
            Log.d(TAG,"Profile Pic Changed");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out = compressImage(imgUrl);

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

                    Log.d(TAG, "onResponse: received information: " );
                    mUsername.setText(response.body().getUser().getUsername());
                    mEmail.setText(response.body().getUser().getEmail());
                    System.out.println(response.body());
                    //data.put(f.getAuthor().getUsername(),"http://192.168.42.179:8000"+f.getPost_pics());
                    //list.add(new Card("http://192.168.42.206:8000"+f.getPost_pics(),f.getAuthor().getUsername()));
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
                        }

                        if (error.has("country")){
                            JSONArray emailArray = new JSONArray(error.get("country").toString());
                            items.add(emailArray.getString(0));
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
                imgUrl = data.getStringExtra("imgUrl");
                UniversalImageLoader.setImage(imgUrl, mProfilePhoto, null,"");
                imgUrl = imgUrl.replace("file://","");
                profilePicChanged = true;

            }
        }
    }













    public ByteArrayOutputStream compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        //FileOutputStream out = null;
        String filename = getFilename();
        //out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        return out;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }




}