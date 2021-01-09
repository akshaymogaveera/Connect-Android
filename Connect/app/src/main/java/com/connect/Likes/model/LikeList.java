package com.connect.Likes.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LikeList {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("person")
    @Expose
    private Person person;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("post")
    @Expose
    private Integer post;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getPost() {
        return post;
    }

    public void setPost(Integer post) {
        this.post = post;
    }

}
