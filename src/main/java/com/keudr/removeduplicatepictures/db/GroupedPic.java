package com.keudr.removeduplicatepictures.db;

public class GroupedPic {

    String value;
    int count;

    public GroupedPic(String v, int c){
        this.value = v;
        this.count=c;
    }

    public String getValue() {
        return value;
    }

    public int getCount() {
        return count;
    }
}
