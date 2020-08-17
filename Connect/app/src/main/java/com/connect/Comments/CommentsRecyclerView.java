package com.connect.Comments;

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

import com.connect.Comments.models.CommentLinear;
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

import de.hdodenhof.circleimageview.CircleImageView;


class CommentsRecyclerView extends RecyclerView.Adapter<CommentsRecyclerView.CommentsListViewHolder> {


    private static final String TAG = "CommentsRecyclerView";

    private Context mContext;
    private int mResource;
    private ArrayList<CommentLinear> list;
    private HashMap<String, CommentLinear> mapping;
    private int lastPosition = -1;

    public CommentsRecyclerView(Context mContext, int resource, ArrayList<CommentLinear> list, HashMap<String, CommentLinear> mapping) {
        this.mContext = mContext;
        this.mResource = resource;
        this.list = list;
        this.mapping = mapping;

        setupImageLoader();
    }

    @NonNull
    @Override
    public CommentsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(mResource, parent, false);
        return new CommentsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsListViewHolder holder, int position) {

        CommentLinear card = list.get(position);

        try{

            lastPosition = position;

            holder.commentedby.setText(card.getAuthor());
            holder.commentContent.setText(card.getText());

            //create the imageloader object

            ImageLoader imageLoader = ImageLoader.getInstance();

            int defaultImage = mContext.getResources().getIdentifier("@drawable/image_failed",null,mContext.getPackageName());

            //com.connect.NewsFeed.NewsFeedFragment newsFeedFragment = new com.connect.NewsFeed.NewsFeedFragment();



            //create display options
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(defaultImage)
                    .showImageOnFail(defaultImage)
                    .showImageOnLoading(defaultImage).build();

            //download and display image from url
            imageLoader.displayImage(card.getProfile_pic(), holder.mProfilePhoto, options,new ImageLoadingListener() {
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

    class CommentsListViewHolder extends RecyclerView.ViewHolder{

        TextView commentedby, commentContent;
        CircleImageView mProfilePhoto;
        ProgressBar dialog;

        public CommentsListViewHolder(@NonNull View itemView) {
            super(itemView);

            commentedby = (TextView) itemView.findViewById(R.id.commentedby);
            commentContent =  (TextView) itemView.findViewById(R.id.commentContent);
            mProfilePhoto = (CircleImageView) itemView.findViewById(R.id.profile_photo_comments);
            dialog = (ProgressBar) itemView.findViewById(R.id.cardProgressDialog);

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
