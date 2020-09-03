package com.connect.NewsFeed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.connect.Comments.ViewAllCommentsActivity;
import com.connect.Profile.UserProfileActivity;
import com.connect.UserProfileEdit.models.UserProfile;
import com.connect.main.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;

import static com.connect.Post.CreatePostApiCall.deletePost;


public class NewsFeedRecyclerView extends RecyclerView.Adapter<NewsFeedRecyclerView.NewsFeedViewHolder> {


    private static final String TAG = "NewFeedRecyclerView";

    private Context mContext;
    private int mResource;
    private ArrayList<Card> list;
    private HashMap<String, Card> mapping;
    private int lastPosition = -1;

    public NewsFeedRecyclerView(Context mContext, int resource, ArrayList<Card> list, HashMap<String, Card> mapping) {
        this.mContext = mContext;
        this.mResource = resource;
        this.list = list;
        this.mapping = mapping;

        setupImageLoader();

    }


    @NonNull
    @Override
    public NewsFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(mResource, parent, false);
        return new NewsFeedViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull NewsFeedViewHolder holder, int position) {

        Card card = list.get(position);

        try{

            lastPosition = position;


            holder.title.setText(card.getTitle());
            holder.commentCount.setText(card.getCountComments());
            holder.likesCount.setText(card.getCountLikes());
            holder.caption.setText(card.getCaption());

            if(Integer.valueOf(card.getCountComments()) > 0){
                Log.d(TAG," view all comments: "+card.getCountComments());
                holder.viewAllComments.setVisibility(View.VISIBLE);
            }
            else{
                holder.viewAllComments.setVisibility(View.GONE);
            }


            if(card.isLiked()){
                holder.likeImage.setImageResource(R.drawable.heartfull);
            }
            else {
                holder.likeImage.setImageResource(R.drawable.heartempty);
            }

            //create the imageloader object
            ImageLoader imageLoader = ImageLoader.getInstance();

            int defaultImage = mContext.getResources().getIdentifier("@drawable/image_failed",null,mContext.getPackageName());


            if(! mContext.toString().contains("ProfileActivity") || mContext.toString().contains("UserProfileActivity"))
                holder.toolbar.getMenu().getItem(1).setVisible(false);

            //delete Post

            holder.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()) {

                        case R.id.delete:
                            deletePost(mContext, card.getId());

//                            Card temp = mapping.get(card.getId());
//                            int pos = list.indexOf(temp);
//                            mapping.remove(card.getId());
//                            list.remove(pos);
//                            holder.itemView.setVisibility(View.GONE);
//                            notifyItemChanged(position);
                            //holder.itemView.setLayoutParams(new CardView.LayoutParams(0,0));
                            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                            params.height = 0;
                            holder.itemView.setLayoutParams(params);
                            //holder.itemView.setLayoutParams(new CardView.LayoutParams(0,0));
                            return true;

                    }


                    return false;
                }

            });



            holder.likeImage.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    int countlikes = Integer.valueOf(card.getCountLikes());
                    Log.d(TAG, "Like button clicked");

                    if(card.isLiked()){
                        holder.likeImage.setImageResource(R.drawable.heartempty);
                        com.connect.Likes.LikeOperations.getCallPostLike(card.getId());
                        card.setCountLikes(String.valueOf(countlikes-1));
                        card.setLiked(false);
                        holder.likesCount.setText(card.getCountLikes());
                        //notifyItemChanged(position);
                        //newsFeedFragment.changeLikesCount(card.getId(),mapping,list);
                    }
                    else {
                        holder.likeImage.setImageResource(R.drawable.heartfull);
                        com.connect.Likes.LikeOperations.getCallPostLike(card.getId());
                        card.setCountLikes(String.valueOf(countlikes+1));
                        card.setLiked(true);
                        holder.likesCount.setText(card.getCountLikes());
                        //mapping.replace(card.getId(),card);
                        //list.set(position,card);
                        //notifyItemChanged(position);
                        //newsFeedFragment.changeLikesCount(card.getId(),mapping,list);
                    }
                }
            });

            holder.viewAllComments.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "View All comments button clicked");
                    Intent intent = new Intent(mContext, ViewAllCommentsActivity.class);
                    intent.putExtra("post_id",card.getId());
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
                    //startActivity(intent);
                    mContext.startActivity(intent);

                }
            });

            //Go to users profile

            holder.cardTitle.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "View All comments button clicked");
                    Intent intent = new Intent(mContext, UserProfileActivity.class);
                    intent.putExtra("id",card.getAuthorid());
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
                    //startActivity(intent);
                    mContext.startActivity(intent);

                }
            });

            //create display options
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(defaultImage)
                    .showImageOnFail(defaultImage)
                    .showImageOnLoading(defaultImage).build();

            //download and display image from url
            imageLoader.displayImage(card.getImgURL(), holder.image, options,new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    holder.dialog.setVisibility(View.VISIBLE);
                }
                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    holder.dialog.setVisibility(View.GONE);
                    holder.image.setVisibility(View.GONE);
                }
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.dialog.setVisibility(View.GONE);
                }
                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }}

            );


            //download and display image from url
            imageLoader.displayImage(card.getProfileImgUrl(), holder.profile_photo_newsfeed, options,new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    holder.dialog.setVisibility(View.VISIBLE);
                }
                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    holder.dialog.setVisibility(View.GONE);
                }
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.dialog.setVisibility(View.GONE);
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

    class NewsFeedViewHolder extends RecyclerView.ViewHolder{

        TextView title, likesCount, commentCount, viewAllComments, caption, cardTitle;
        ImageView image, likeImage, profile_photo_newsfeed;
        ProgressBar dialog;
        MenuItem deleteOption;
        Toolbar toolbar;


        public NewsFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            cardTitle = (TextView) itemView.findViewById(R.id.cardTitle);
            title = (TextView) itemView.findViewById(R.id.cardTitle);
            image = (ImageView) itemView.findViewById(R.id.cardImage);
            profile_photo_newsfeed= (ImageView) itemView.findViewById(R.id.profile_photo_newsfeed);
            dialog = (ProgressBar) itemView.findViewById(R.id.cardProgressDialog);
            likesCount = (TextView)  itemView.findViewById(R.id.cardLikes);
            commentCount = (TextView)  itemView.findViewById(R.id.cardComments);
            likeImage = (ImageView) itemView.findViewById(R.id.likeImage);
            viewAllComments =  (TextView) itemView.findViewById(R.id.viewcomments);
            caption = (TextView)  itemView.findViewById(R.id.caption);
            deleteOption = itemView.findViewById(R.id.delete);
            toolbar = itemView.findViewById(R.id.linearabove);


        }
    }

    /**
     * Required for setting up the Universal Image loader Library
     */
    private void setupImageLoader(){
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP
    }





//    private static final String TAG = "NewFeedRecyclerView";
//
//    private Context mContext;
//    private int mResource;
//    private int lastPosition = -1;
//
//    public NewFeedRecyclerView(Context mContext) {
//        this.mContext = mContext;
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return null;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull  RecyclerView.ViewHolder holder, int position) {
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return 0;
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder{
//
//        TextView title, likesCount, commentCount;
//        ImageView image;
//        ProgressBar dialog;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            title = (TextView) itemView.findViewById(R.id.cardTitle);
//            image = (ImageView) itemView.findViewById(R.id.cardImage);
//            dialog = (ProgressBar) itemView.findViewById(R.id.cardProgressDialog);
//            likesCount = (TextView)  itemView.findViewById(R.id.cardLikes);
//            commentCount = (TextView)  itemView.findViewById(R.id.cardComments);
//
//
//        }
//    }
}
