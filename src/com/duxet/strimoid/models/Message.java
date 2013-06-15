package com.duxet.strimoid.models;

public class Message {
    
    boolean unread;
    String user, userAvatar, entry, entryId, time;
    
    public Message(String user, String userAvatar, String entry,
            String entryId, String date, boolean unread) {
        super();
        this.user = user;
        this.userAvatar = userAvatar;
        this.entry = entry;
        this.entryId = entryId;
        this.time = date;
        this.unread = unread;
    }

    public String getUser() {
        return user;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public String getEntry() {
        return entry;
    }
    
    public String getEntryId() {
        return entryId;
    }

    public String getTime() {
        return time;
    }
    
    public boolean isUnread() {
        return isUnread();
    }

}
