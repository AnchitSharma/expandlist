package com.example.lenovo.explistdemo;

import java.util.ArrayList;

/**
 * Created by Lenovo on 19-08-2018.
 */

public class Group {

    private int groupId;
    private String Name;
    private ArrayList<Child> Items;


    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ArrayList<Child> getItems() {
        return Items;
    }

    public void setItems(ArrayList<Child> items) {
        Items = items;
    }
}
