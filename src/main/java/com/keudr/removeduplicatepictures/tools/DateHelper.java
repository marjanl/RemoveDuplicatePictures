package com.keudr.removeduplicatepictures.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {


    /*
    * //        DateTime: '2018:01:26 18:14:41'
  //      DateTimeOriginal: '2018:01:26 18:14:41'
  * convert thse to date
    * */
    static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

    public static Date convertToDate(String s) throws ParseException {
        if(s != null){
            try {
                return sdf.parse(s.replace("DateTime: ", "")
                        .replace("DateTimeOriginal: ", "")
                        .replace(",", "")
                        .replace("'", "")
                        .trim());
            } catch (Exception e){
                System.out.println("ex while parsing date: "+ s +"...msg:"+e.getLocalizedMessage() );
            }
        }
        return null;
    }

    public static String formatDate(Date d){
        if(d == null) return null;
        return sdf.format(d);
    }
}

