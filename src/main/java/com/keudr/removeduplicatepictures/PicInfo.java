package com.keudr.removeduplicatepictures;

import com.keudr.removeduplicatepictures.tools.DateHelper;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PicInfo {

    int id;
    String filePath;
    String fileName;
    String md5Hash;
    Date fileCreated;
    Date dateTime;
    Date dateTimeOriginal;


    public PicInfo(String fPath, String fName, String md5, Date dCreated, Date dTime, Date dtOriginal){
        filePath = fPath;
        fileName = fName;
        md5Hash = md5;
        fileCreated=dCreated;
        dateTime = dTime;
        dateTimeOriginal = dtOriginal;
    }

    public String dumpToString(){
        return "path="+filePath + ", name="+fileName +", md5="+md5Hash+", fileCreated="+fileCreated+", dateTime="+dateTime+", dateTimeOriginal"+dateTimeOriginal;
    }

    public int getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMd5Hash() {
        return md5Hash;
    }

    public Date getFileCreated() {
        return fileCreated;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public Date getDateTimeOriginal() {
        return dateTimeOriginal;
    }

    public ImageView getImage() {
        System.out.println("delam getImage:"+filePath);
        Image image = new Image(new File(filePath).toURI().toString(), 200,200,true, false);
        System.out.println("dobil image");
        ImageView imageView = new ImageView(image);
        return imageView;
    }

    public String getFileCreatedFormated() {
        return DateHelper.formatDate(fileCreated);
    }

    public String getDateTimeFormated() {
        return DateHelper.formatDate(dateTime);
    }

    public String getDateTimeOriginalFormated() {
        return DateHelper.formatDate(dateTimeOriginal);
    }

}
