package com.duxet.strimoid.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.duxet.strimoid.utils.Session;

public class Content implements Voting, Parcelable {

    String id, title, author, url, imageUrl, time, strim;
    int upvotes, downvotes, comments, color;
    boolean isUpvoted, isDownvoted;

    public Content(String id, String title, String author, String url,
            String imageUrl, String time, String strim,
            int upvotes, int downvotes,
            int comments, int color, boolean isUpvoted, boolean isDownvoted) {
        super();
        this.id = id;
        this.title = title;
        this.author = author;
        this.url = url;
        this.imageUrl = imageUrl;
        this.time = time;
        this.strim = strim;
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
        return this.strim + "/t/" + this.id;
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

    public String getLikeUrl() {
        return "ajax/t/" + this.id + "/lubie?token=" + Session.getToken();
    }

    public String getDislikeUrl() {
        return "ajax/t/" + this.id + "/nielubie?token=" + Session.getToken();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {        
        out.writeString(id);
        out.writeString(title);
        out.writeString(author);
        out.writeString(url);
        out.writeString(imageUrl);
        out.writeString(time);
        out.writeString(strim);
        out.writeInt(upvotes);
        out.writeInt(downvotes);
        out.writeInt(comments);
        out.writeInt(color);
        out.writeValue(isUpvoted);
        out.writeValue(isDownvoted);  
    }
    
    public static final Parcelable.Creator<Content> CREATOR = new Parcelable.Creator<Content>() {
        public Content createFromParcel(Parcel in) {
            return new Content(in);
        }

        public Content[] newArray(int size) {
            return new Content[size];
        }
    };

    private Content(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.author = in.readString();
        this.url = in.readString();
        this.imageUrl = in.readString();
        this.time = in.readString();
        this.strim = in.readString();
        this.upvotes = in.readInt();
        this.downvotes = in.readInt();
        this.comments = in.readInt();
        this.color = in.readInt();
        this.isUpvoted = (Boolean) in.readValue(null);
        this.isDownvoted = (Boolean) in.readValue(null);
    }

}
