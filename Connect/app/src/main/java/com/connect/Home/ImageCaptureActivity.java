package com.connect.Home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.connect.Utils.Permissions;
import com.connect.main.R;
import com.connect.main.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


/**
 * @author Hasangi Thathsarani
 */

public class ImageCaptureActivity extends AppCompatActivity {

    private ImageView imageView;
    final int PIC_CROP = 2;
    private static final String TAG = "ImageCaptureActivity";
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    private Context mContext = ImageCaptureActivity.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());

        //imageView = (ImageView) findViewById(R.id.my_avatar);
        String imageViewId = getIntent().getStringExtra("imageViewId");
        Log.d("TAG" ,"ID : "+imageViewId);

        int id = getResources().getIdentifier(imageViewId,"string",getPackageName());
        Log.d("TAG" ,"ID : "+id+" "+R.id.my_avatar);

        int parent=0;
        //View otherView = View.inflate(R.layout.snippet_center_editprofile,mContext,false);

        imageView = findViewById(id);
        selectImage(ImageCaptureActivity.this);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                selectImage(ImageCaptureActivity.this);
//            }
//        });

    }

    private void selectImage(Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture").setCancelable(false);

        builder.setItems(options, new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {


                    if(checkPermissionsArray(Permissions.PERMISSIONS)){
                        Log.d("TAG" ,"gallery called");
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, 1);//one can be replaced with any action code

                    }else{
                        verifyPermissions(Permissions.PERMISSIONS);
                    }



                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                    finish();
                }
                else{
                    dialog.dismiss();
                    finish();
                }
            }


        });
        builder.show();
    }


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println(requestCode+"====================="+CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE+"   "+RESULT_OK+"="+resultCode);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Intent intent = new Intent();
                intent.putExtra("imgUrl", resultUri.toString());
                setResult(RESULT_OK, intent);
                finish();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        imageView.setImageBitmap(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                Log.d(TAG,picturePath);

                                //UniversalImageLoader.setImage("file://"+picturePath, imageView, null, "");
                                // UniversalImageLoader.setImage("file://"+picturePath, imageView, null, "");

                                cursor.close();

                                //send image to previous activity
//                                Intent intent = new Intent();
//                                intent.putExtra("imgUrl", "file://"+picturePath);
//                                setResult(RESULT_OK, intent);

                                launchImageCrop(Uri.parse("file://"+picturePath));

                                //finish();

                                //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));




                            }
                        }

                    }
                    break;

            }
        }
        else if (resultCode == 0){
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_capture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(
                ImageCaptureActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    /**
     * Check an array of permissions
     * @param permissions
     * @return
     */
    public boolean checkPermissionsArray(String[] permissions){
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for(int i = 0; i< permissions.length; i++){
            String check = permissions[i];
            if(!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }

    /**
     * Check a single permission is it has been verified
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(ImageCaptureActivity.this, permission);

        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        }
        else{
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }

    private void launchImageCrop(Uri uri){
        CropImage.activity(uri)
                .setAspectRatio(1,1)
                .setGuidelines(CropImageView.Guidelines.ON)
                //.setAspectRatio(1920, 1080)
                //.setCropShape(CropImageView.CropShape.RECTANGLE) // default is rectangle
                .start(this);
    }
}

