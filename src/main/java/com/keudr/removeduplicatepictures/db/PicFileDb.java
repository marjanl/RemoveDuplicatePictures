package com.keudr.removeduplicatepictures.db;

import com.keudr.removeduplicatepictures.PicInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.Date;
import java.util.List;

public class PicFileDb {


    public final Connection conn;
    PreparedStatement ps,psDelete, psGroupBy;
    ObservableList<PicInfo> data = FXCollections.observableArrayList();
    ObservableList<GroupedPic> dataGroupBy = FXCollections.observableArrayList();

    public PicFileDb() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:pic.db");
        if (conn != null) {
            DatabaseMetaData meta = conn.getMetaData();

            conn.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS  failed_pic (\n" +
                    "\tid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\tfile_path text NULL,\n" +
                    "\tex_msg text NULL\n" +
                    ");");
            conn.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS  pic_file (\n" +
                    "\tid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\tfile_path text NULL,\n" +
                    "\tfile_name text NULL,\n" +
                    "\tmd5_hash text NULL,\n" +
                    "\tfile_date DATETIME NULL,\n" +
                    "\tpic_date DATETIME NULL,\n" +
                    "\tpic_original_date DATETIME NULL\n" +
                    ");");
            System.out.println("Tables were created.");

        }
        ps=conn.prepareStatement("select * from pic_file pf where md5_hash = ? order by file_path ");
        psDelete=conn.prepareStatement("delete from pic_file where file_path=?");
        psGroupBy=conn.prepareStatement("select md5_hash , count(*) as c, min(file_name) from pic_file pf group by md5_hash  having count(*) > 1 ");
    }

    public List<PicInfo> getData(String md5Hash) throws SQLException {
        data = FXCollections.observableArrayList();
        ps.setString(1, md5Hash);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            data.add(new PicInfo(rs.getString("file_path"), rs.getString("file_name"), rs.getString("md5_hash"),
                    rs.getTimestamp("file_date"), rs.getTimestamp("pic_date"), rs.getTimestamp("pic_original_date")));
        }
        return data;
    }

    public void deleteByPath(PicInfo picInfo) throws SQLException {
        psDelete.clearParameters();
        psDelete.setString(1, picInfo.getFilePath());
        psDelete.executeUpdate();
    }

    public List<GroupedPic> getGroupByData() throws SQLException {
        dataGroupBy = FXCollections.observableArrayList();
        ResultSet rs = psGroupBy.executeQuery();
        while(rs.next()){
            dataGroupBy.add(new GroupedPic(rs.getString(1), rs.getInt(2), rs.getString(3)));
        }
        return dataGroupBy;
    }
}
