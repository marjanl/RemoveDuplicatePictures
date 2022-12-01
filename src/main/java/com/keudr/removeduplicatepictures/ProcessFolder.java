package com.keudr.removeduplicatepictures;

import com.keudr.removeduplicatepictures.tools.ImageReader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Stream;

import static org.apache.commons.imaging.Imaging.getMetadata;


public class ProcessFolder {

    final File dirToProcess;
    Connection conn;
    ImageReader imageReader;
    PreparedStatement ps, failedPs;
    int counter = 0;

    public ProcessFolder(File dir) throws SQLException {
        dirToProcess=dir;
        conn = ApplicationStart.conn;
        imageReader = new ImageReader();
        ps = conn.prepareStatement("insert into pic_file(id,file_path, file_name, md5_hash, file_date, pic_date, pic_original_date)" +
                " values (nextval('pic_file_id_seq'), ?, ?, ?, ?, ?, ?)");
        failedPs = conn.prepareStatement("insert into failed_pic(file_path, ex_msg)" +
                " values (?, ?)");
    }

    public void process() throws IOException, SQLException {

        conn.createStatement().executeUpdate( "truncate pic_file" );
        conn.createStatement().executeUpdate( "truncate failed_pic" );

        if (!dirToProcess.isDirectory()) throw new RuntimeException("Not a valid directory");
        System.out.println("started "+new java.util.Date());
        try (Stream<Path> walk = Files.walk(dirToProcess.toPath())) {
            walk
                    .filter(Files::isRegularFile)   // is a file
                    .filter(p -> p.getFileName().toString().endsWith(".jpg") || p.getFileName().toString().endsWith(".JPG"))
                    .forEach(c -> saveToDb(c));
        }
        ps.executeBatch();
        System.out.println("ended +"+new java.util.Date());
    }

    public void saveToDb(Path path)  {
        PicInfo picInfo = null;
        try {
            picInfo = imageReader.getPicInfo(path);
            ps.setString(1, picInfo.filePath);
            ps.setString(2, picInfo.fileName);
            ps.setString(3, picInfo.md5Hash);
            ps.setDate(4, new java.sql.Date(picInfo.fileCreated.getTime()));
            if(picInfo.dateTime != null ){
                ps.setTimestamp(5, new java.sql.Timestamp(picInfo.dateTime.getTime()));
            }else {
                ps.setTimestamp(5, null);
            }
            if(picInfo.dateTimeOriginal != null ){
                ps.setTimestamp(6, new java.sql.Timestamp(picInfo.dateTimeOriginal.getTime()));
            }else{
                ps.setTimestamp(6, null);
            }
            ps.addBatch();
            counter++;
            if(counter % 1000 ==0){
                counter=0;
                ps.executeBatch();
            }
        } catch (SQLException e) {
            System.out.println("SQLEx: file "+path.toAbsolutePath()+"! Message:"+e.getLocalizedMessage());
        }catch (Exception e) {
            try {
                failedPs.setString(1, path.toAbsolutePath().toString());
                failedPs.setString(2, e.getLocalizedMessage());
                failedPs.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("SQL EX while writing failed "+ex.getLocalizedMessage());;
            }
            System.out.println("Ex: file "+path.toAbsolutePath()+"! Message:"+e.getLocalizedMessage());
        }


    }

}
