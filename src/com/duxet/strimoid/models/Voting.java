package com.duxet.strimoid.models;

public interface Voting {
    public int getUpvotes();
    public int getDownvotes();
    public void setUpvotes(int upvotes);
    public void setDownvotes(int downvotes);
    public boolean isUpvoted();
    public boolean isDownvoted(); 
    public void setUpvoted(boolean isUpvoted);
    public void setDownvoted(boolean isDownvoted);
    public String getLikeUrl();
    public String getDislikeUrl();
}
