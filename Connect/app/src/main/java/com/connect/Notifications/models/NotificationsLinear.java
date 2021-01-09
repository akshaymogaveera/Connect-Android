package com.connect.Notifications.models;

import java.util.Date;

public class NotificationsLinear implements Comparable<NotificationsLinear>{

    String postId;
    String authorId;
    String username;
    String count;
    String text;
    String postImgUrl;
    String profilePicUrl;
    Date date;

    public NotificationsLinear(String postId, String authorId, String username, String count, String text, String postImgUrl, String profilePicUrl, Date date) {
        this.postId = postId;
        this.authorId = authorId;
        this.username = username;
        this.count = count;
        this.text = text;
        this.postImgUrl = postImgUrl;
        this.profilePicUrl = profilePicUrl;
        this.date = date;
    }

    @Override
    public int compareTo(NotificationsLinear o) {
        return getDate().compareTo(o.getDate());
    }



    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostImgUrl() {
        return postImgUrl;
    }

    public void setPostImgUrl(String postImgUrl) {
        this.postImgUrl = postImgUrl;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


}
