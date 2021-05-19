package com.connect.Notifications;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.connect.Comments.ViewAllCommentsActivity;
import com.connect.Likes.LikesListActivity;
import com.connect.Notifications.models.NotificationsLinear;
import com.connect.Profile.UserProfileActivity;
import com.connect.main.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


class NotificationRecyclerView extends RecyclerView.Adapter<NotificationRecyclerView.NotificationViewHolder> {


    private static final String TAG = "NotificationRecyclerView";

    private Context mContext;
    private int mResource;
    private String userId;
    private ArrayList<NotificationsLinear> list;
    private HashMap<String, NotificationsLinear> mapping;
    private int lastPosition = -1;

    public NotificationRecyclerView(Context mContext, int resource, ArrayList<NotificationsLinear> list, HashMap<String, NotificationsLinear> mapping, String userId) {
        this.mContext = mContext;
        this.mResource = resource;
        this.list = list;
        this.mapping = mapping;
        this.userId = userId;

        com.connect.Utils.ImageLoader imageLoader = new com.connect.Utils.ImageLoader();
        imageLoader.setupImageLoader(mContext);
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(mResource, parent, false);
        return new NotificationViewHolder(view);
    }




    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {

        NotificationsLinear card = list.get(position);

        try{


            PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
            String ago = prettyTime.format(card.getDate());
            holder.timeAgo.setText(ago);

            lastPosition = position;

            holder.notificationtext.setText(card.getText());

            //create the imageloader object

            ImageLoader imageLoader = ImageLoader.getInstance();

            int defaultImage = mContext.getResources().getIdentifier("@drawable/image_failed",null,mContext.getPackageName());

            //com.connect.NewsFeed.NewsFeedFragment newsFeedFragment = new com.connect.NewsFeed.NewsFeedFragment();

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(card.getText().contains("like")){

                        Log.d(TAG, "onClick: profile clicked");
                        Intent intent = new Intent(mContext, LikesListActivity.class);
                        intent.putExtra("post_id",card.getPostId().replace("l",""));
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
                        //startActivity(intent);
                        mContext.startActivity(intent);

                    }
                    else if(card.getText().contains("comment")){

                        Log.d(TAG, "onClick: comment clicked");
                        Intent intent = new Intent(mContext, ViewAllCommentsActivity.class);
                        intent.putExtra("post_id",card.getPostId().replace("c",""));
                        intent.putExtra("authorId", userId);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
                        //startActivity(intent);
                        mContext.startActivity(intent);

                    }
                    else if(card.getText().contains("request")){

                        Log.d(TAG, "onClick: profile clicked");
                        Intent intent = new Intent(mContext, UserProfileActivity.class);
                        intent.putExtra("id",card.getAuthorId());
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
                        //startActivity(intent);
                        mContext.startActivity(intent);

                    }



                }
            });

            //create display options
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(defaultImage)
                    .showImageOnFail(defaultImage)
                    .showImageOnLoading(defaultImage).build();

            //download and display image from url
            imageLoader.displayImage(card.getProfilePicUrl(), holder.profile_photo_notification, options,new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    //holder.dialog.setVisibility(View.VISIBLE);
                }
                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    //holder.dialog.setVisibility(View.GONE);
                }
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    //holder.dialog.setVisibility(View.GONE);
                }
                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }}

            );

            //download and display image from url
            imageLoader.displayImage(card.getPostImgUrl(), holder.post_pic_notification, options,new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    //holder.dialog.setVisibility(View.VISIBLE);
                }
                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    //holder.dialog.setVisibility(View.GONE);
                }
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    //holder.dialog.setVisibility(View.GONE);
                }
                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }}

            );

        }catch (IllegalArgumentException e){
            System.out.println(e);
            Log.e(TAG, "getView: IllegalArgumentException: " + e.getMessage() );
        }


}

    @Override
    public int getItemCount() {
        return list.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder{

        TextView notificationtext, timeAgo;
        CircleImageView profile_photo_notification;
        ImageView post_pic_notification;
        ProgressBar dialog;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            timeAgo =  itemView.findViewById(R.id.timeAgo);
            notificationtext =  itemView.findViewById(R.id.notificationtext);
            profile_photo_notification = (CircleImageView) itemView.findViewById(R.id.profile_photo_notification);
            post_pic_notification =  itemView.findViewById(R.id.post_pic_notification);
            dialog = (ProgressBar) itemView.findViewById(R.id.cardProgressDialog);

        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) { return position; }

}
