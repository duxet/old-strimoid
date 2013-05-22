package com.duxet.strimoid.models;

public class Content {

    String id, title, author, desc, url, imageUrl, commentsUrl;
    String likeUrl, dislikeUrl;
    int upvotes, downvotes;
    
    public Content(String id, String title, String author, String desc,
            String url, String imageUrl, String commentsUrl, String likeUrl,
            String dislikeUrl, int upvotes, int downvotes) {
        super();
        this.id = id;
        this.title = title;
        this.author = author;
        this.desc = desc;
        this.url = url;
        this.imageUrl = imageUrl;
        this.commentsUrl = commentsUrl;
        this.likeUrl = likeUrl;
        this.dislikeUrl = dislikeUrl;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDesc() {
        return desc;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCommentsUrl() {
        return commentsUrl;
    }

    public String getLikeUrl() {
        return likeUrl;
    }

    public String getDislikeUrl() {
        return dislikeUrl;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

}
