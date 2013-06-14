package com.duxet.strimoid.models;

public class User {

    // User info
    boolean subscribed, blocked;
    String name, desc, nick, sex, city, online, joined;
    int age;
    
    // Reputation
    Reputation reputation;
    
    // Achievements count
    int gold, silver, brown;
    
    // Count of items posted
    int contents, entries, comments;
    
    // Other
    int subscribers, moderated;

    public User(String name, String desc, String nick, String sex, String online, String joined,
            Reputation reputation, int gold, int silver, int brown, int contents, int entries,
            int comments, int subscribers, int moderated) {
        this.subscribed = false;
        this.blocked = false;
        this.name = name;
        this.nick = nick;
        this.sex = sex;
        this.online = online;
        this.joined = joined;
        this.reputation = reputation;
        this.gold = gold;
        this.silver = silver;
        this.brown = brown;
        this.contents = contents;
        this.entries = entries;
        this.comments = comments;
        this.subscribers = subscribers;
        this.moderated = moderated;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getNick() {
        return nick;
    }

    public String getSex() {
        return sex;
    }

    public String getCity() {
        return city;
    }

    public String getOnline() {
        return online;
    }

    public String getJoined() {
        return joined;
    }

    public int getAge() {
        return age;
    }

    public Reputation getReputation() {
        return reputation;
    }

    public int getGold() {
        return gold;
    }

    public int getSilver() {
        return silver;
    }

    public int getBrown() {
        return brown;
    }

    public int getContents() {
        return contents;
    }

    public int getEntries() {
        return entries;
    }

    public int getComments() {
        return comments;
    }

    public int getSubscribers() {
        return subscribers;
    }

    public int getModerated() {
        return moderated;
    }

}
