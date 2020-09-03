package com.connect.Search.model;

public class SearchLinear {

    private String profile_pic;
    private String first_name;
    private String last_name;
    private String author_id;

    public SearchLinear(String profile_pic, String first_name, String last_name, String author_id) {
        this.profile_pic = profile_pic;
        this.first_name = first_name;
        this.last_name = last_name;
        this.author_id = author_id;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

}
