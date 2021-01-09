package com.connect.Post;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.connect.Home.ImageCaptureActivity;
import com.connect.Utils.CompressImage;
import com.connect.main.R;
import com.connect.main.UniversalImageLoader;

import java.io.ByteArrayOutputStream;

import static com.connect.Post.CreatePostApiCall.createPost;

public class CreatePostActivity extends AppCompatActivity {

    private static final String TAG = "CreatePostActivity";
    SharedPreferences sharedpreferences;
    ImageView pic, backArrow;
    String imgUrl="";
    Button continueBtn;
    ByteArrayOutputStream postImagearray = new ByteArrayOutputStream();
    EditText caption;
    CompressImage mCompressImage;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        mCompressImage = new CompressImage();
        mContext = CreatePostActivity.this;
        pic = (ImageView) findViewById(R.id.postpic);
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: changing profile photo");
                Intent intent = new Intent(CreatePostActivity.this, ImageCaptureActivity.class);
                imgUrl="";
                postImagearray = new ByteArrayOutputStream();
                intent.putExtra("imageViewId","profile_photo");
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
                //startActivity(intent);
                startActivityForResult(intent, 1);
                //getActivity().finish();
            }
        });

        //back arrow for navigating back to "ProfileActivity"
        backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                finish();
            }
        });

        caption = findViewById(R.id.caption);
        continueBtn = findViewById(R.id.continuebutton);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: create post called");

                if(!(caption.getText().length() > 0)){
                    Toast.makeText(mContext, "Please write a caption", Toast.LENGTH_SHORT).show();
                }
                else if(imgUrl.isEmpty()){
                    Toast.makeText(mContext, "Please choose an image", Toast.LENGTH_SHORT).show();
                }
                else{
                    //System.out.println(imgUrl+" test "+postImagearray.toString());
                    //createPost(mContext, caption.getText().toString(),imgUrl,postImagearray);
                    createPost(mContext, caption.getText().toString(),imgUrl,mCompressImage.getPostImagearray());
                    Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                    finish();
                }


            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {

                imgUrl = data.getStringExtra("imgUrl");

                String out=null;
                try {
                    out = "file://"+mCompressImage.compressImageForPost(mContext,imgUrl);
                    //out = "file://"+compressImage(imgUrl);
                    //System.out.println("PATH ========"+imgUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UniversalImageLoader.setImage(out, pic, null,"");
                //imgUrl = imgUrl.replace("file://","");
                //System.out.println(imgUrl+" | "+data.getStringExtra("imgUrl"));

            }
        }
    }


}