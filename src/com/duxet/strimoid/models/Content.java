package com.duxet.strimoid.models;

public class Content {

    private String title, author, desc, url, imageUrl, commentsUrl;
    private int upvotes, downvotes;

    public Content(String title, String author, String desc, String url,
            String imageUrl, String commentsUrl, int upvotes, int downvotes) {
        super();
        this.title = title;
        this.author = author;
        this.setDesc(desc);
        this.url = url;
        this.imageUrl = imageUrl;
        this.commentsUrl = commentsUrl;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCommentsUrl() {
        return commentsUrl;
    }

    public void setCommentsUrl(String commentsUrl) {
        this.commentsUrl = commentsUrl;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

}
