package com.duxet.strimoid.models;

public class Entry implements Voting {

    String id, author, avatar, message, time, strim;
    String likeUrl, dislikeUrl, moreUrl;
    int upvotes, downvotes, color;
    boolean isUpvoted, isDownvoted, isReply;

    public Entry(String id, String author, String avatar,
            String message, String time, String strim, String likeUrl,
            String dislikeUrl, String moreUrl, int upvotes, int downvotes,
            int color, boolean isUpvoted, boolean isDownvoted, boolean isReply) {
        super();
        this.id = id;
        this.author = author;
        this.avatar = avatar;
        this.message = message;
        this.time = time;
        this.strim = strim;
        this.likeUrl = likeUrl;
        this.dislikeUrl = dislikeUrl;
        this.moreUrl = moreUrl;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.color = color;
        this.isUpvoted = isUpvoted;
        this.isDownvoted = isDownvoted;
        this.isReply = isReply;
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

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getStrim() {
        return strim;
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
    
    public boolean isLoadMore() {
        return !moreUrl.equals("");
    }

    public String getMoreUrl() {
        return moreUrl;
    }

}
