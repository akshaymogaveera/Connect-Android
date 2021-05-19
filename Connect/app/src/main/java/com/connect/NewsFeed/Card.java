package com.connect.NewsFeed;

public class Card {
    private String authorid;
    private String id;
    private String imgURL;
    private String title;
    private String countLikes;
    private String countComments;
    private boolean liked;
    private String caption;
    private String profileImgUrl;
    private String createdDate;

    public Card(String authorid, String id, String imgURL, String title, String countLikes, String countComments, boolean liked, String caption, String profileImgUrl, String createdDate) {
        this.authorid = authorid;
        this.id = id;
        this.imgURL = imgURL;
        this.title = title;
        this.countLikes = countLikes;
        this.countComments = countComments;
        this.liked = liked;
        this.caption = caption;
        this.profileImgUrl = profileImgUrl;
        this.createdDate = createdDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getAuthorid() {
        return authorid;
    }

    public void setAuthorid(String authorid) {
        this.authorid = authorid;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public String getCountLikes() {
        return countLikes;
    }

    public void setCountLikes(String countLikes) {
        this.countLikes = countLikes;
    }

    public String getCountComments() {
        return countComments;
    }

    public void setCountComments(String countComments) {
        this.countComments = countComments;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}