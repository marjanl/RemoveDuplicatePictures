package com.keudr.removeduplicatepictures;

import com.keudr.removeduplicatepictures.db.PicFileDb;
import com.keudr.removeduplicatepictures.tools.ImageReader;
import com.keudr.removeduplicatepictures.tools.MD5Hash;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Stream;


public class ProcessFolder {

    final File dirToProcess;
    ImageReader imageReader;
    PreparedStatement ps, failedPs;
    int counter = 0;
    PicFileDb picFileDb;
    MD5Hash md5Hash = new MD5Hash();

    public ProcessFolder(File dir) throws SQLException {
        picFileDb = new PicFileDb();
        dirToProcess = dir;
        imageReader = new ImageReader();
        ps = picFileDb.conn.prepareStatement("insert into pic_file(file_path, file_name, md5_hash, file_date, pic_date, pic_original_date)" +
                " values (?, ?, ?, ?, ?, ?)");
        failedPs = picFileDb.conn.prepareStatement("insert into failed_pic(file_path, ex_msg)" +
                " values (?, ?)");
    }

    public void process() throws IOException, SQLException {
        picFileDb.conn.createStatement().executeUpdate("delete from pic_file");
        picFileDb.conn.createStatement().executeUpdate("delete from failed_pic");
        if (!dirToProcess.isDirectory()) throw new RuntimeException("Not a valid directory");
        System.out.println("started " + new java.util.Date());
        try (Stream<Path> walk = Files.walk(dirToProcess.toPath())) {
            walk
                    .filter(Files::isRegularFile)   // is a file
                    .filter(p -> p.getFileName().toString().endsWith(".jpg") || p.getFileName().toString().endsWith(".JPG")
                            || p.getFileName().toString().endsWith(".avi")
                            || p.getFileName().toString().endsWith(".AVI")
                            || p.getFileName().toString().endsWith(".mp4")
                            || p.getFileName().toString().endsWith(".MP4")
                    )
                    .forEach(c -> saveToDb(c));
        }
        ps.executeBatch();
        System.out.println("ended +" + new java.util.Date());
    }

    public void saveToDb(Path path) {
        PicInfo picInfo = null;
        try {
            if (path.getFileName().toString().endsWith(".avi")
                    || path.getFileName().toString().endsWith(".AVI")
                    || path.getFileName().toString().endsWith(".mp4")
                    || path.getFileName().toString().endsWith(".MP4")) {
                picInfo = new PicInfo(path.toAbsolutePath().toString(), path.getFileName().toString(),
                        md5Hash.getChecksum(path),
                        new java.util.Date(((FileTime) Files.getAttribute(path, "creationTime")).toMillis()),
                        null, null);
            } else {
                picInfo = imageReader.getPicInfo(path);
            }
            ps.setString(1, picInfo.filePath);
            ps.setString(2, picInfo.fileName);
            ps.setString(3, picInfo.md5Hash);
            ps.setDate(4, new java.sql.Date(picInfo.fileCreated.getTime()));
            if (picInfo.dateTime != null) {
                ps.setTimestamp(5, new java.sql.Timestamp(picInfo.dateTime.getTime()));
            } else {
                ps.setTimestamp(5, null);
            }
            if (picInfo.dateTimeOriginal != null) {
                ps.setTimestamp(6, new java.sql.Timestamp(picInfo.dateTimeOriginal.getTime()));
            } else {
                ps.setTimestamp(6, null);
            }
            ps.addBatch();
            counter++;
            if (counter % 1000 == 0) {
                counter = 0;
                ps.executeBatch();
            }
        } catch (SQLException e) {
            System.out.println("SQLEx: file " + path.toAbsolutePath() + "! Message:" + e.getLocalizedMessage());
        } catch (Exception e) {
            try {
                failedPs.setString(1, path.toAbsolutePath().toString());
                failedPs.setString(2, e.getLocalizedMessage());
                failedPs.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("SQL EX while writing failed " + ex.getLocalizedMessage());
                ;
            }
            System.out.println("Ex: file " + path.toAbsolutePath() + "! Message:" + e.getLocalizedMessage());
        }


    }

}
