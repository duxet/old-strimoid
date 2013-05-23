package com.duxet.strimoid.models;

public class Comment implements Voting {
    
    String id, author, avatar, text, time;
    String likeUrl, dislikeUrl;
    int upvotes, downvotes, color;
    boolean isUpvoted, isDownvoted, isReply;

    public Comment(String id, String author, String avatar, String text,
            String time, String likeUrl, String dislikeUrl, int upvotes,
            int downvotes, boolean isUpvoted, boolean isDownvoted,
            boolean isReply, int color) {
        super();
        this.id = id;
        this.author = author;
        this.avatar = avatar;
        this.text = text;
        this.time = time;
        this.likeUrl = likeUrl;
        this.dislikeUrl = dislikeUrl;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.isUpvoted = isUpvoted;
        this.isDownvoted = isDownvoted;
        this.isReply = isReply;
        this.color = color;
    }
    
    public String getId() {
        return id;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public int getAuthorColor() {
        return color;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public String getText() {
        return text;
    }
    
    public String getTime() {
        return time;
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
    
    public boolean isUpvoted() {
        return isUpvoted;
    }
    
    public boolean isDownvoted() {
        return isDownvoted;
    }
    
    public boolean isReply() {
        return isReply;
    }

    public void setUpvoted(boolean isUpvoted) {
        this.isUpvoted = isUpvoted;
    }

    public void setDownvoted(boolean isDownvoted) {
        this.isDownvoted = isDownvoted;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

}
