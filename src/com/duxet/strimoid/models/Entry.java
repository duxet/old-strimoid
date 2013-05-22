package com.duxet.strimoid.models;

public class Entry {

    String id, author, avatar, message, time, strim;
    int upvotes, downvotes;
    boolean isReply;

    public Entry(String id, String author, String avatar, String message, String time, String strim,
            boolean isReply, int upvotes, int downvotes) {
        super();
        this.id = id;
        this.author = author;
        this.avatar = avatar;
        this.message = message;
        this.time = time;
        this.strim = strim;
        this.isReply = isReply;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getStrim() {
        return strim;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public boolean isReply() {
        return isReply;
    }

}
