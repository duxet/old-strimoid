package com.duxet.strimoid.models;

public class Reputation {
    
    // Points
    int total, contents, entries, comments;
    
    // Levels
    int lvlTotal, lvlContents, lvlEntries, lvlComments;

    public Reputation(int total, int contents, int entries, int comments, int lvlTotal, int lvlContents,
            int lvlEntries, int lvlComments) {
        super();
        this.total = total;
        this.contents = contents;
        this.entries = entries;
        this.comments = comments;
        this.lvlTotal = lvlTotal;
        this.lvlContents = lvlContents;
        this.lvlEntries = lvlEntries;
        this.lvlComments = lvlComments;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setContents(int contents) {
        this.contents = contents;
    }

    public void setEntries(int entries) {
        this.entries = entries;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public void setLvlTotal(int lvlTotal) {
        this.lvlTotal = lvlTotal;
    }

    public void setLvlContents(int lvlContents) {
        this.lvlContents = lvlContents;
    }

    public void setLvlEntries(int lvlEntries) {
        this.lvlEntries = lvlEntries;
    }

    public void setLvlComments(int lvlComments) {
        this.lvlComments = lvlComments;
    }

}
