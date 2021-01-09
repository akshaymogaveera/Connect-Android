package com.connect.Friends.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Friend {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user1")
    @Expose
    private User1 user1;
    @SerializedName("user2")
    @Expose
    private User2 user2;
    @SerializedName("friend_status")
    @Expose
    private Integer friendStatus;
    @SerializedName("created_date")
    @Expose
    private String createdDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User1 getUser1() {
        return user1;
    }

    public void setUser1(User1 user1) {
        this.user1 = user1;
    }

    public User2 getUser2() {
        return user2;
    }

    public void setUser2(User2 user2) {
        this.user2 = user2;
    }

    public Integer getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(Integer friendStatus) {
        this.friendStatus = friendStatus;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

}