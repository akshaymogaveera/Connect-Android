package com.connect.Notifications.models;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Notification {

    @SerializedName("postID")
    @Expose
    private String postID;
    @SerializedName("personID")
    @Expose
    private Integer personID;
    @SerializedName("personUsername")
    @Expose
    private String personUsername;
    @SerializedName("PostImgUrl")
    @Expose
    private String postImgUrl;
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("date")
    @Expose
    private String date;

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public Integer getPersonID() {
        return personID;
    }

    public void setPersonID(Integer personID) {
        this.personID = personID;
    }

    public String getPersonUsername() {
        return personUsername;
    }

    public void setPersonUsername(String personUsername) {
        this.personUsername = personUsername;
    }

    public String getPostImgUrl() {
        return postImgUrl;
    }

    public void setPostImgUrl(String postImgUrl) {
        this.postImgUrl = postImgUrl;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}