package com.duxet.strimoid.models;

public class Content {

    private String id, title, author, desc, url, imageUrl, commentsUrl;
    private int upvotes, downvotes;

    public Content(String id, String title, String author, String desc, String url,
            String imageUrl, String commentsUrl, int upvotes, int downvotes) {
        super();
        this.id = id;
        this.title = title;
        this.author = author;
        this.desc = desc;
        this.url = url;
        this.imageUrl = imageUrl;
        this.commentsUrl = commentsUrl;
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

    public int getUpvotes() {
        return upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

}
