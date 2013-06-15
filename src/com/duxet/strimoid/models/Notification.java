package com.duxet.strimoid.models;

public class Notification {
    
    boolean unread;
    String type, text, time;

    public Notification(String type, String text, String time, boolean unread) {
        super();
        this.type = type;
        this.text = text;
        this.time = time;
        this.unread = unread;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }
    
    public boolean isUnread() {
        return unread;
    }

}
