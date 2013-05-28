package com.duxet.strimoid.models;

public class Content implements Voting {

    String id, title, author, url, imageUrl, commentsUrl, time, strim;
    String likeUrl, dislikeUrl;
    int upvotes, downvotes, comments, color;
    boolean isUpvoted, isDownvoted;

    public Content(String id, String title, String author, String url,
            String imageUrl, String commentsUrl, String time, String strim,
            String likeUrl, String dislikeUrl, int upvotes, int downvotes,
            int comments, int color, boolean isUpvoted, boolean isDownvoted) {
        super();
        this.id = id;
        this.title = title;
        this.author = author;
        this.url = url;
        this.imageUrl = imageUrl;
        this.commentsUrl = commentsUrl;
        this.time = time;
        this.strim = strim;
        this.likeUrl = likeUrl;
        this.dislikeUrl = dislikeUrl;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.comments = comments;
        this.color = color;
        this.isUpvoted = isUpvoted;
        this.isDownvoted = isDownvoted;
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
    
    public int getAuthorColor() {
        return color;
    }
    
    public String getStrim() {
    	return strim;
    }
    
    public String getTime() {
    	return time;
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
    
    public int getComments() {
        return comments;
    }

    public boolean isUpvoted() {
        return isUpvoted;
    }

    public boolean isDownvoted() {
        return isDownvoted;
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
