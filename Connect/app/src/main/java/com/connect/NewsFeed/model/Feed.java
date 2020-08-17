package com.connect.NewsFeed.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 5/1/2017.
 */

public class Feed {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("created_date")
    @Expose
    private String created_date;

    @SerializedName("post_pics")
    @Expose
    private String post_pics;


    @SerializedName("author")
    @Expose
    private Author author;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getPost_pics() {
        return post_pics;
    }

    public void setPost_pics(String post_pics) {
        this.post_pics = post_pics;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Feed{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", created_date='" + created_date + '\'' +
                ", post_pics='" + post_pics + '\'' +
                ", author=" + author +
                '}';
    }
}