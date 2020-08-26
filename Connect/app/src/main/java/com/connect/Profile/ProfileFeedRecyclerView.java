package com.connect.Profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.connect.Comments.ViewAllCommentsActivity;
import com.connect.NewsFeed.Card;
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


class ProfileFeedRecyclerView extends RecyclerView.Adapter<ProfileFeedRecyclerView.NewsFeedViewHolder> {


    private static final String TAG = "NewFeedRecyclerView";

    private Context mContext;
    private int mResource;
    private ArrayList<Card> list;
    private HashMap<String, Card> mapping;
    private int lastPosition = -1;

    public ProfileFeedRecyclerView(Context mContext, int resource, ArrayList<Card> list, HashMap<String, Card> mapping) {
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

            //com.connect.NewsFeed.NewsFeedFragment newsFeedFragment = new com.connect.NewsFeed.NewsFeedFragment();



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

        TextView title, likesCount, commentCount, viewAllComments, caption;
        ImageView image, likeImage;
        ProgressBar dialog;

        public NewsFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.cardTitle);
            image = (ImageView) itemView.findViewById(R.id.cardImage);
            dialog = (ProgressBar) itemView.findViewById(R.id.cardProgressDialog);
            likesCount = (TextView)  itemView.findViewById(R.id.cardLikes);
            commentCount = (TextView)  itemView.findViewById(R.id.cardComments);
            likeImage = (ImageView) itemView.findViewById(R.id.likeImage);
            viewAllComments =  (TextView) itemView.findViewById(R.id.viewcomments);
            caption = (TextView)  itemView.findViewById(R.id.caption);


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
