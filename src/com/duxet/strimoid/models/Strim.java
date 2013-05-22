package com.duxet.strimoid.models;

public class Strim {
    
    String name, title, desc;

    public Strim(String name, String title, String desc) {
        super();
        this.name = name;
        this.title = title;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

}
