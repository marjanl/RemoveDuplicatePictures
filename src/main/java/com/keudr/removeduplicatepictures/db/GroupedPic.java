package com.keudr.removeduplicatepictures.db;

public class GroupedPic {

    String value, sampleFileName;
    int count;



    public GroupedPic(String v, int c, String sfn){
        this.value = v;
        this.count=c;
        this.sampleFileName = sfn;
    }

    public String getValue() {
        return value;
    }
    public int getCount() {
        return count;
    }
    public String getSampleFileName() {
        return sampleFileName;
    }
}
