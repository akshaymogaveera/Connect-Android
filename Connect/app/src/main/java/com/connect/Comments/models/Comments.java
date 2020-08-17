package com.connect.Comments.models;

import com.connect.NewsFeed.model.Author;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comments {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("author")
    @Expose
    private Author author;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("approved_comment")
    @Expose
    private Boolean approvedComment;
    @SerializedName("post")
    @Expose
    private Integer post;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getApprovedComment() {
        return approvedComment;
    }

    public void setApprovedComment(Boolean approvedComment) {
        this.approvedComment = approvedComment;
    }

    public Integer getPost() {
        return post;
    }

    public void setPost(Integer post) {
        this.post = post;
    }

}