package com.duxet.strimoid.models;

public class Comment {
    
    String author, avatar, text, time;
    int upvotes, downvotes;
    boolean isReply;
    
    public Comment(String author, String avatar, String text, String time,
            int upvotes, int downvotes, boolean isReply) {
        super();
        this.author = author;
        this.avatar = avatar;
        this.text = text;
        this.time = time;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.isReply = isReply;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public Boolean isReply() {
        return isReply;
    }

    public void setIsReply(boolean isReply) {
        this.isReply = isReply;
    }
        
}
