package com.connect.Comments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.connect.Comments.model.CommentLinear;
import com.connect.main.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


class CommentsRecyclerView extends RecyclerView.Adapter<CommentsRecyclerView.CommentsListViewHolder> {


    private static final String TAG = "CommentsRecyclerView";

    private Context mContext;
    private int mResource;
    private ArrayList<CommentLinear> list;
    private HashMap<String, CommentLinear> mapping;
    private int lastPosition = -1;
    private String postAuthorId, userId, userName;
    

    public CommentsRecyclerView(Context mContext, int resource, ArrayList<CommentLinear> list, HashMap<String, CommentLinear> mapping,String postAuthorId, String userId, String userName) {
        this.mContext = mContext;
        this.mResource = resource;
        this.list = list;
        this.mapping = mapping;
        this.postAuthorId = postAuthorId;
        this.userId = userId;
        this.userName = userName;

        com.connect.Utils.ImageLoader imageLoader = new com.connect.Utils.ImageLoader();
        imageLoader.setupImageLoader(mContext);
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

            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(postAuthorId.equals(userId) || userName.equals(card.getAuthor()) ) {
                        card.setSelected(!card.isSelected());
                        v.setBackgroundColor(card.isSelected() ? Color.CYAN : Color.WHITE);
                    }
                    else{
                        Log.e(TAG, "Cannot select comment, post or comment doesn't belong to user " );
                    }
                }
            });

            lastPosition = position;

            holder.commentedby.setText(card.getAuthor());
            holder.commentContent.setText(card.getText());

            //create the imageloader object

            ImageLoader imageLoader = ImageLoader.getInstance();

            int defaultImage = mContext.getResources().getIdentifier("@drawable/image_failed",null,mContext.getPackageName());

            //com.connect.NewsFeed.NewsFeedFragment newsFeedFragment = new com.connect.NewsFeed.NewsFeedFragment();




            String[] datelist = card.getCreated_on().split("\\.");
            String[] datelist1 = card.getCreated_on().split("\\+");
            String date ="date";
            if(datelist.length > 1) {
                //System.out.println(datelist.length+"Count -----------"+datelist[0]);
                date = datelist[0] + "Z";
            }
            else if(datelist1.length > 1){
                date = datelist1[0] + "Z";
            }
            else{
                date  = card.getCreated_on();
            }



            Date parsedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(date);
            PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
            String ago = prettyTime.format(parsedDate);
            holder.timeAgo.setText(ago);

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
                    Log.e(TAG, "onLoadingFailed: Something went wrong: " + failReason.toString());
                }
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    //holder.dialog.setVisibility(View.GONE);
                }
                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    Log.e(TAG, "onLoadingCancelled: Something went wrong: " );

                }}

            );

        }catch (IllegalArgumentException e){
            System.out.println(e);
            Log.e(TAG, "getView: IllegalArgumentException: " + e.getMessage() );
        }
        catch (Exception e){
            System.out.println(e);
            Log.e(TAG, "getView: Exception: " + e.getMessage() );
        }


}

    @Override
    public int getItemCount() {
        return list.size();
    }

    class CommentsListViewHolder extends RecyclerView.ViewHolder{

        TextView commentedby, commentContent, timeAgo;
        CircleImageView mProfilePhoto;
        ProgressBar dialog;
        LinearLayout linearLayout;

        public CommentsListViewHolder(@NonNull View itemView) {
            super(itemView);

            commentedby = (TextView) itemView.findViewById(R.id.commentedby);
            commentContent =  (TextView) itemView.findViewById(R.id.commentContent);
            mProfilePhoto = (CircleImageView) itemView.findViewById(R.id.profile_photo_comments);
            dialog = (ProgressBar) itemView.findViewById(R.id.cardProgressDialog);
            timeAgo =  (TextView) itemView.findViewById(R.id.timeAgo);
            linearLayout = itemView.findViewById(R.id.linearLayout2);

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
