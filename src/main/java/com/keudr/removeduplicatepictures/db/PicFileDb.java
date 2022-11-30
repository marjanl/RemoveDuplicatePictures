package com.keudr.removeduplicatepictures.db;

import com.keudr.removeduplicatepictures.PicInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class PicFileDb {

    private final Connection conn;
    PreparedStatement ps;
    ObservableList<PicInfo> data = FXCollections.observableArrayList();
    public PicFileDb(Connection conn) throws SQLException {
        assert !conn.isClosed();
        this.conn=conn;
        ps=conn.prepareStatement("select * from pic_file pf where md5_hash = ? order by 1 ");
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

}
