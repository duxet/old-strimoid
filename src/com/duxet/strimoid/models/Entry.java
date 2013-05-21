package com.duxet.strimoid.models;

public class Entry {
	
	String author, avatar, message, time, strim;
	int upvotes, downvotes;
	Boolean isReply;
	
	public Entry(String author, String avatar, String message, String time, String strim,
			Boolean isReply, int upvotes, int downvotes) {
		super();
		this.author = author;
		this.avatar = avatar;
		this.message = message;
		this.time = time;
		this.strim = strim;
		this.isReply = isReply;
		this.upvotes = upvotes;
		this.downvotes = downvotes;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getStrim() {
		return strim;
	}

	public void setStrim(String strim) {
		this.strim = strim;
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

	public void setReply(boolean isReply) {
		this.isReply = isReply;
	}

}
