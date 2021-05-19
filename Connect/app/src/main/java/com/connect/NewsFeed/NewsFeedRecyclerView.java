package com.connect.NewsFeed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.connect.Comments.ViewAllCommentsActivity;
import com.connect.Home.HomeActivity;
import com.connect.Likes.LikesListActivity;
import com.connect.Profile.UserProfileActivity;
import com.connect.main.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.connect.Post.CreatePostApiCall.deletePost;


public class NewsFeedRecyclerView extends RecyclerView.Adapter<NewsFeedRecyclerView.NewsFeedViewHolder> {


    private static final String TAG = "NewsFeedRecyclerView";

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

        com.connect.Utils.ImageLoader imageLoader = new com.connect.Utils.ImageLoader();
        imageLoader.setupImageLoader(mContext);

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

            String[] datelist = card.getCreatedDate().split("\\.");
            String[] datelist1 = card.getCreatedDate().split("\\+");
            String date ="date";
            if(datelist.length > 1) {
                //System.out.println(datelist.length+"Count -----------"+datelist[0]);
                date = datelist[0] + "Z";
            }
            else if(datelist1.length > 1){
                date = datelist1[0] + "Z";
            }
            else {
                date = card.getCreatedDate();
            }
            Date parsedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(date);
            PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
            String ago = prettyTime.format(parsedDate);
            holder.timeAgo.setText(ago);
            //Log.d(TAG, " Date --------------------------  "+ago);

//            if(Integer.valueOf(card.getCountComments()) > 0){
//                Log.d(TAG," view all comments: "+card.getCountComments());
//                holder.viewAllComments.setVisibility(View.VISIBLE);
//            }
//            else{
//                holder.viewAllComments.setVisibility(View.GONE);
//            }


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

            holder.cardLikes.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {

                    if(Integer.parseInt(card.getCountLikes()) > 0){
                        Log.d(TAG, "View All likes button clicked");
                        Intent intent = new Intent(mContext, LikesListActivity.class);
                        intent.putExtra("post_id",card.getId());
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
                        //startActivity(intent);
                        mContext.startActivity(intent);
                    }

                }
            });

            holder.commentImage.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {

                    Log.d(TAG, "View All comments button clicked");
                    Intent intent = new Intent(mContext, ViewAllCommentsActivity.class);
                    intent.putExtra("post_id", card.getId());
                    intent.putExtra("authorId", card.getAuthorid());
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
                    //startActivity(intent);
                    mContext.startActivity(intent);


                }
            });

            //Go to users profile

            if(mContext.toString().contains("Home"))
            {
                holder.cardTitle.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Profile Name clicked");

                        if(HomeActivity.getId().equals(card.getAuthorid())){

                            Log.d(TAG, "Same User clicked");

                        }
                        else{

                            Intent intent = new Intent(mContext, UserProfileActivity.class);
                            intent.putExtra("id",card.getAuthorid());
                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
                            //startActivity(intent);
                            mContext.startActivity(intent);

                        }
                    }
                });
            }


            //create display options
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(defaultImage)
                    .showImageOnFail(defaultImage)
                    .showImageOnLoading(defaultImage).build();

            //download and display image from url

            holder.image.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    holder.image.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    //Load an image to your IMAGEVIEW here

                    imageLoader.displayImage(card.getImgURL(), holder.image, options,new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            holder.dialog.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            holder.dialog.setVisibility(View.GONE);
                            holder.image.setVisibility(View.GONE);
                            Log.d(TAG, "Image Loading failed");
                            holder.dialog.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            holder.dialog.setVisibility(View.GONE);
                            Log.d(TAG, "Image Loading Complete");
                            Log.d(TAG, card.getCaption()+"  "+card.getImgURL().toString());
                        }
                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {
                            Log.d(TAG, "Image Loading Cancelled");

                        }}

                    );
                }
            });



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

        }catch (IllegalArgumentException | ParseException e){
            System.out.println(e);
            Log.e(TAG, "getView: IllegalArgumentException: " + e.getMessage() );
        }


}

    @Override
    public int getItemCount() {
        return list.size();
    }

    class NewsFeedViewHolder extends RecyclerView.ViewHolder{

        TextView title, likesCount, commentCount, cardLikes, caption, cardTitle, timeAgo;
        ImageView image, likeImage, profile_photo_newsfeed, commentImage;
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
            commentImage = (ImageView) itemView.findViewById(R.id.commentImage);
            cardLikes =  (TextView) itemView.findViewById(R.id.cardLikes);
            caption = (TextView)  itemView.findViewById(R.id.caption);
            timeAgo = (TextView)  itemView.findViewById(R.id.timeAgo);
            deleteOption = itemView.findViewById(R.id.delete);
            toolbar = itemView.findViewById(R.id.linearabove);



        }
    }



    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {

        return position;

    }

}
