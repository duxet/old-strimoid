package com.duxet.strimoid.models;

import java.util.ArrayList;

public class Strim {
    
    String name, title, desc;
    Boolean isGroup;
    ArrayList<Strim> childrens;

    public Strim(String name, String title, String desc, Boolean isGroup) {
        super();
        this.name = name;
        this.title = title;
        this.desc = desc;
        this.isGroup = isGroup;
        this.childrens = new ArrayList<Strim>();
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

    public Boolean isGroup() {
        return isGroup;
    }
    
    public void addChildren(Strim strim) {
        childrens.add(strim);
    }
    
    public void addChildrens(ArrayList<Strim> strims) {
        childrens.addAll(strims);
    }

    public ArrayList<Strim> getChildrens() {
        return childrens;
    }
 
}
