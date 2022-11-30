package com.keudr.removeduplicatepictures.db;

import com.keudr.removeduplicatepictures.PicInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupedByHash {

    private final Connection conn;
    PreparedStatement ps;
    ObservableList<GroupedPic> data = FXCollections.observableArrayList();
    public GroupedByHash(Connection conn) throws SQLException {
        assert !conn.isClosed();
        this.conn=conn;
        ps=conn.prepareStatement("select md5_hash , count(*) as c from pic_file pf group by md5_hash  having count(*) > 1 ");
    }

    public List<GroupedPic> getData() throws SQLException {
        data = FXCollections.observableArrayList();
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            data.add(new GroupedPic(rs.getString(1), rs.getInt(2)));
        }
        return data;
    }





}
