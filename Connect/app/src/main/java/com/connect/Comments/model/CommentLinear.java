package com.connect.Comments.model;

public class CommentLinear {

    private String profile_pic;
    private String author;
    private String text;
    private String created_on;

    public CommentLinear(String profile_pic, String author, String text, String created_on) {
        this.profile_pic = profile_pic;
        this.author = author;
        this.text = text;
        this.created_on = created_on;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }
}
