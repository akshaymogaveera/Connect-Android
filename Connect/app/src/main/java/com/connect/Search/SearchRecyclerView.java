package com.connect.Search;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.connect.Comments.models.CommentLinear;
import com.connect.Profile.UserProfileActivity;
import com.connect.Search.model.Search;
import com.connect.Search.model.SearchLinear;
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


class SearchRecyclerView extends RecyclerView.Adapter<SearchRecyclerView.SearchViewHolder> {


    private static final String TAG = "SearchRecyclerView";

    private Context mContext;
    private int mResource;
    private ArrayList<SearchLinear> list;
    private HashMap<String, SearchLinear> mapping;
    private int lastPosition = -1;

    public SearchRecyclerView(Context mContext, int resource, ArrayList<SearchLinear> list, HashMap<String, SearchLinear> mapping) {
        this.mContext = mContext;
        this.mResource = resource;
        this.list = list;
        this.mapping = mapping;

        setupImageLoader();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(mResource, parent, false);
        return new SearchViewHolder(view);
    }




    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {

        SearchLinear card = list.get(position);

        try{

            lastPosition = position;

            holder.firstname.setText(card.getFirst_name());
            holder.lastname.setText(card.getLast_name());

            //create the imageloader object

            ImageLoader imageLoader = ImageLoader.getInstance();

            int defaultImage = mContext.getResources().getIdentifier("@drawable/image_failed",null,mContext.getPackageName());

            //com.connect.NewsFeed.NewsFeedFragment newsFeedFragment = new com.connect.NewsFeed.NewsFeedFragment();

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: profile clicked");
                    Intent intent = new Intent(mContext, UserProfileActivity.class);
                    intent.putExtra("id",card.getAuthor_id());
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
            imageLoader.displayImage(card.getProfile_pic(), holder.profile_photo_search, options,new ImageLoadingListener() {
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

    class SearchViewHolder extends RecyclerView.ViewHolder{

        TextView firstname, lastname;
        CircleImageView profile_photo_search;
        ProgressBar dialog;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            firstname =  itemView.findViewById(R.id.firstname);
            lastname =  itemView.findViewById(R.id.lastname);
            profile_photo_search = (CircleImageView) itemView.findViewById(R.id.profile_photo_search);
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



}
